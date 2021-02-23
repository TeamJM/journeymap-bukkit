package info.journeymap.bukkit.network

import info.journeymap.bukkit.JourneyMapBukkit
import info.journeymap.bukkit.WORLD_ID_CHANNEL
import org.bukkit.Bukkit.getServer
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer

class PacketHandler(val plugin: JourneyMapBukkit) : PluginMessageListener {
    init {
        getServer().messenger.registerOutgoingPluginChannel(plugin, WORLD_ID_CHANNEL)
        getServer().messenger.registerIncomingPluginChannel(plugin, WORLD_ID_CHANNEL, this)
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel.toLowerCase() == WORLD_ID_CHANNEL) {
            this.sendWorldId(player.world.uid.toString(), player)
        }
    }

    fun broadcastWorldId(worldID: String) {
        for (player: Player in getServer().onlinePlayers) {
            this.sendWorldId(worldID, player)
        }
    }

    fun sendWorldId(worldID: String, player: Player) {
        try {
            this.sendPacket(player, 0.toByte(), worldID.toByteArray(), WORLD_ID_CHANNEL)
        } catch (e: UnsupportedEncodingException) {
            this.plugin.logger.severe("Unsupported encoding: UTF-*")
        }
    }

    fun sendPacket(player: Player, packetID: Byte, data: ByteArray, channel: String) {
        val buffer = ByteBuffer.allocate(44 + data.size)

        buffer.put(packetID).put(42).put(data.size.toByte()).put(data)
        player.sendPluginMessage(this.plugin, channel, buffer.array())
    }
}