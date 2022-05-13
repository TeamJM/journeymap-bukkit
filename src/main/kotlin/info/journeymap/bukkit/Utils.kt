package info.journeymap.bukkit

import org.bukkit.Bukkit
import org.bukkit.World

/**
 * @return UID of the primary world
 */
public fun getPrimaryWorldId(): String = Bukkit.getWorlds().first().uid.toString()

/**
 * @return UID of the given world or null if world does not exist
 * @throws WorldNotFoundException when provided worldName does not correspond to a world
 */
public fun getWorldId(worldName: String): String = getWorld(worldName).uid.toString()

/**
 * @throws WorldNotFoundException when provided worldName does not correspond to a world
 */
public fun getWorld(worldName: String): World {
    val world = Bukkit.getWorlds().find { world -> world.name == worldName }

    if (world !== null) {
        return world
    }

    throw WorldNotFoundException(worldName)
}
