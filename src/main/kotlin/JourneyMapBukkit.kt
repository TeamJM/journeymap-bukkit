package info.journeymap.bukkit

import info.journeymap.bukkit.network.PacketHandler
import org.bukkit.plugin.java.JavaPlugin

class JourneyMapBukkit : JavaPlugin() {
    lateinit var packetHandler: PacketHandler
    lateinit var eventHandler: EventHandler

    override fun onEnable() {
        super.onEnable()

        this.packetHandler = PacketHandler(this)
        this.eventHandler = EventHandler(this)

        server.pluginManager.registerEvents(this.eventHandler, this)
    }
}