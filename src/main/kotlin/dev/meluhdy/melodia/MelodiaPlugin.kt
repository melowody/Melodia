package dev.meluhdy.melodia

import dev.meluhdy.melodia.command.CommandManager
import dev.meluhdy.melodia.command.MelodiaCommand
import org.bukkit.plugin.java.JavaPlugin

abstract class MelodiaPlugin : JavaPlugin() {

    companion object {
        var instance: JavaPlugin? = null
            private set
    }

    override fun onEnable() {
        instance = this
        Melodia.plugin = this

        onPluginEnable()

        for (command in registerCommands()) {
            CommandManager.addCommand(command)
        }
        CommandManager.registerCommands()
    }

    abstract fun onPluginEnable()

    abstract fun registerCommands(): ArrayList<MelodiaCommand>

}