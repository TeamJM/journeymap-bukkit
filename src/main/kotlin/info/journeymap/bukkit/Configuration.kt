package info.journeymap.bukkit

import org.bukkit.Bukkit
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.time.LocalDateTime

private const val WORLDS_KEY = "worlds"
private const val MIGRATION_KEY = "legacy_configuration_migrated"

public class Configuration(private val plugin: JourneyMapBukkit) {

    public fun init() {
        plugin.saveDefaultConfig()
        this.migrateLegacyConfiguration()
        this.validateConfig()

        if (doVerboseLogging()) {
            for (world in Bukkit.getWorlds()) {
                plugin.logger.info("World '${world.name}' uses ID '${resolveWorldId(world.name)}'.")
            }
        }
    }

    public fun resolveWorldId(worldName: String): String? {
        val conf = getWorlds().getConfigurationSection(worldName)
        // send primary by default
        if (conf === null) {
            return getPrimaryWorldId()
        }

        // check if sending disabled
        if (!conf.getBoolean("UseWorldID", true)) {
            return null
        }

        // check for explicit WorldID
        val explicitWorldId = conf.getString("WorldID")
        if (explicitWorldId !== null) {
            return explicitWorldId
        }

        // check for id_from declaration
        val idFrom = conf.getString("id_from")
        if (idFrom !== null) {
            return try {
                getWorldId(idFrom)
            } catch (e: WorldNotFoundException) {
                plugin.logger.severe("Cannot get WorldID for '$worldName': World '${e.worldName}' does not exist.")
                null
            }
        }

        // world is in config but with no relevant configuration
        return getPrimaryWorldId()
    }

    public fun doVerboseLogging(): Boolean = plugin.config.getBoolean("verbose_logging", true)

    private fun validateConfig() {
        for (worldName in getWorlds().getKeys(false)) {
            try {
                getWorld(worldName)
            } catch (e: WorldNotFoundException) {
                plugin.logger.warning("World '$worldName' is specified in configuration but does not exist.")
            }

            val idFrom = getWorlds().getString("$worldName.id_from")
            if (idFrom !== null) {
                try {
                    getWorldId(idFrom)
                } catch (e: WorldNotFoundException) {
                    plugin.logger.severe(
                        "World '$worldName' specifies '$idFrom' for getting its world ID but that world does not exist."
                    )
                }
            }
        }
    }

    private fun getWorlds() = plugin.config.getConfigurationSection(WORLDS_KEY) ?: MemoryConfiguration()

    private fun migrateLegacyConfiguration() {
        if (plugin.config.getBoolean(MIGRATION_KEY, false)) {
            return
        }

        val legacyDataFolder = plugin.dataFolder.parentFile.resolve("JourneyMapServer")
        val files = legacyDataFolder.listFiles { _, name -> name.endsWith(".cfg") }
        if (files === null) {
            // legacy directory does not exist
            plugin.config.set(MIGRATION_KEY, true)
            plugin.saveConfig()
            return
        }

        plugin.logger.info("Migrating legacy JourneyMapServer configuration...")

        val worldsConfig = getWorlds()
        for (file in files) {
            val worldName = file.nameWithoutExtension
            // Yaml is a superset of JSON, so the original JSON configuration can be just read
            val conf = YamlConfiguration.loadConfiguration(file.reader(Charsets.UTF_8))

            // remove config version but keep even values we don't necessarily support yet
            conf.set("ConfigVersion", null)

            if (worldsConfig.get(worldName) !== null) {
                plugin.logger.warning(
                    "Overwriting existing world configuration for '$worldName' with contents of '${file.path}'."
                )
            }

            worldsConfig.set(worldName, conf)

            val comments = ArrayList<String>()
            comments.add("Migrated from '${legacyDataFolder.resolve(worldName)}' on ${LocalDateTime.now()}")
            worldsConfig.setComments(worldName, comments)
        }

        plugin.config.set(WORLDS_KEY, worldsConfig)
        plugin.config.set(MIGRATION_KEY, true)
        plugin.saveConfig()
        plugin.logger.info("Migration finished. You may now remove old configuration folder '$legacyDataFolder'.")
    }
}
