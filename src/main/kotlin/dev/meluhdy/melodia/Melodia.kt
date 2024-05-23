package dev.meluhdy.melodia

import dev.meluhdy.melodia.utils.uuid.UUIDManager
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var melodiaInstance: Melodia

class Melodia : JavaPlugin() {

    override fun onEnable() {
        melodiaInstance = this
        dataFolder.mkdir()
        UUIDManager.load()
    }

    override fun onDisable() {
        UUIDManager.save()
    }

}