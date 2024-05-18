package dev.meluhdy.melodia.utils

import org.bukkit.Bukkit
import org.bukkit.event.Event

/**
 * A collection of utils regarding Bukkit Events.
 */
@Suppress("unused")
object EventUtils {

    /**
     * Call a given event.
     *
     * @param event The event to call.
     */
    fun callEvent(event: Event) {
        Bukkit.getPluginManager().callEvent(event)
    }

}