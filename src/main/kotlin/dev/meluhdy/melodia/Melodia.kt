package dev.meluhdy.melodia

import dev.meluhdy.melodia.utils.UUIDUtils
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var melodiaInstance: Melodia

@Suppress("unused")
class Melodia : JavaPlugin() {

    override fun onEnable() {
        melodiaInstance = this
        dataFolder.mkdir()
        UUIDUtils.load()
    }

    override fun onDisable() {
        UUIDUtils.save()
    }

}