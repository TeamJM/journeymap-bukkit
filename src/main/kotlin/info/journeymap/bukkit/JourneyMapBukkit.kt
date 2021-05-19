package info.journeymap.bukkit

import org.bukkit.plugin.java.JavaPlugin

public class JourneyMapBukkit : JavaPlugin() {
    internal lateinit var packetHandler: PacketHandler
    internal lateinit var eventHandler: EventHandler

    override fun onEnable() {
        super.onEnable()

        this.packetHandler = PacketHandler(this)
        this.eventHandler = EventHandler(this)

        server.pluginManager.registerEvents(this.eventHandler, this)
    }
}
