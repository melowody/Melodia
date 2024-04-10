package dev.meluhdy.melodia

import dev.meluhdy.melodia.command.CommandManager
import dev.meluhdy.melodia.command.MelodiaCommand
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var pluginInstance: MelodiaPlugin
    private set

/**
 * Please inherit this as an object.
 *
 * The base of the plugin, modified to be able to handle MelodiaCommands well.
 */
abstract class MelodiaPlugin : JavaPlugin() {

    /**
     * The list of commands for the plugin to register.
     */
    abstract val commands: ArrayList<MelodiaCommand>

    override fun onEnable() {
        pluginInstance = this
        onPluginEnable()

        for (command in commands) {
            CommandManager.addCommand(command)
        }
        CommandManager.registerCommands()
    }

    /**
     * Write code here to run at the beginning of onEnable.
     */
    abstract fun onPluginEnable()

}