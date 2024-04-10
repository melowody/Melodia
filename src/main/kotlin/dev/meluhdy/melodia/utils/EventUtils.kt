package dev.meluhdy.melodia.utils

import org.bukkit.Bukkit
import org.bukkit.event.Event

@Suppress("unused")
object EventUtils {

    fun callEvent(event: Event) {
        Bukkit.getPluginManager().callEvent(event)
    }

}