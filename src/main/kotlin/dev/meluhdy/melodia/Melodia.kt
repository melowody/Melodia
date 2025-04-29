package dev.meluhdy.melodia

import org.bukkit.plugin.java.JavaPlugin

internal lateinit var melodiaInstance: Melodia

class Melodia : JavaPlugin() {

    override fun onEnable() {
        melodiaInstance = this
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
