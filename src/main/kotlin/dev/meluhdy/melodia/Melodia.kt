package dev.meluhdy.melodia

import dev.meluhdy.melodia.utils.ConsoleLogger
import dev.meluhdy.melodia.utils.LoggingUtils
import org.bukkit.plugin.java.JavaPlugin

class Melodia : JavaPlugin() {

    companion object {
        internal lateinit var melodiaInstance: Melodia
        val logger: ConsoleLogger = ConsoleLogger("Melodia", LoggingUtils.ConsoleLevel.DEBUG)
    }

    override fun onEnable() {
        melodiaInstance = this
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
