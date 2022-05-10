package info.journeymap.bukkit

import org.bukkit.Bukkit.getServer
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer

private const val BUFFER_SIZE_MINIMUM = 44
private const val VOXELMAP_MAGIC_NUMBER = 42.toByte()

public class PacketHandler(private val plugin: JourneyMapBukkit) : PluginMessageListener {
    init {
        getServer().messenger.registerOutgoingPluginChannel(plugin, WORLD_ID_CHANNEL)
        getServer().messenger.registerIncomingPluginChannel(plugin, WORLD_ID_CHANNEL, this)
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel.lowercase() == WORLD_ID_CHANNEL) {
            this.sendWorldId(getPrimaryWorldId(), player)
        }
    }

    public fun broadcastWorldId(worldID: String) {
        for (player: Player in getServer().onlinePlayers) {
            this.sendWorldId(worldID, player)
        }
    }

    public fun sendWorldId(worldID: String, player: Player) {
        this.plugin.logger.info("Sending WorldId " + worldID + " to " + player.name)

        try {
            this.sendPacket(player, 0.toByte(), worldID.toByteArray(), WORLD_ID_CHANNEL)
        } catch (e: UnsupportedEncodingException) {
            this.plugin.logger.severe("Unsupported encoding: UTF-*")
        }
    }

    public fun sendPacket(player: Player, packetID: Byte, data: ByteArray, channel: String) {
        val buffer = ByteBuffer.allocate(BUFFER_SIZE_MINIMUM + data.size)

        buffer.put(packetID).put(VOXELMAP_MAGIC_NUMBER).put(data.size.toByte()).put(data)
        player.sendPluginMessage(this.plugin, channel, buffer.array())
    }
}
