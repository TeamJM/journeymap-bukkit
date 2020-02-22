package info.journeymap.bukkit

import org.bukkit.Bukkit.getServer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent

class EventHandler(val plugin: JourneyMapBukkit) : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onJoin(event: PlayerJoinEvent) {
        getServer().scheduler.callSyncMethod(this.plugin) {
            this.plugin.packetHandler.sendWorldId(event.player.world.uid.toString(), event.player)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onWorldChange(event: PlayerChangedWorldEvent) {
        getServer().scheduler.callSyncMethod(this.plugin) {
            this.plugin.packetHandler.sendWorldId(event.player.world.uid.toString(), event.player)
        }
    }
}