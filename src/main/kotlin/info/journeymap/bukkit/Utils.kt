package info.journeymap.bukkit

import org.bukkit.Bukkit

public fun getPrimaryWorldId(): String = Bukkit.getWorlds().first().uid.toString()
