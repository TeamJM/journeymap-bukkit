package info.journeymap.bukkit

import org.bukkit.Bukkit.getServer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent

public class EventHandler(private val plugin: JourneyMapBukkit) : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public fun onJoin(event: PlayerJoinEvent) {
        getServer().scheduler.callSyncMethod(this.plugin) {
            this.plugin.packetHandler.sendWorldId(getPrimaryWorldId(), event.player)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public fun onWorldChange(event: PlayerChangedWorldEvent) {
        getServer().scheduler.callSyncMethod(this.plugin) {
            this.plugin.packetHandler.sendWorldId(getPrimaryWorldId(), event.player)
        }
    }
}
