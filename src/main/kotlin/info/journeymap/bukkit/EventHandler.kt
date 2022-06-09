package info.journeymap.bukkit

import org.bukkit.Bukkit.getServer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent

public class EventHandler(private val plugin: JourneyMapBukkit) : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public fun onJoin(event: PlayerJoinEvent): Unit = sendWorldId(event.player)

    @EventHandler(priority = EventPriority.MONITOR)
    public fun onWorldChange(event: PlayerChangedWorldEvent): Unit = sendWorldId(event.player)

    private fun sendWorldId(player: Player) {
        getServer().scheduler.callSyncMethod(this.plugin) {
            val worldID = plugin.configuration.resolveWorldId(player.world.name)

            if (worldID !== null) {
                this.plugin.packetHandler.sendWorldId(worldID, player)
            }
        }
    }
}
