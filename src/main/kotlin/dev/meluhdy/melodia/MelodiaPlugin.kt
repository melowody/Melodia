package dev.meluhdy.melodia

import dev.meluhdy.melodia.command.MelodiaCommand
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin

/**
 * The base plugin, made to handle MelodiaCommands well
 */
abstract class MelodiaPlugin : JavaPlugin() {

    /**
     * The list of MelodiaCommands for the plugin to listen to
     */
    abstract fun getCommands() : ArrayList<MelodiaCommand>

    override fun onEnable() {

        lifecycleManager.registerEventHandler(
            LifecycleEvents.COMMANDS
        ) { commands ->
            getCommands().forEach { command ->
                println("Registering ${command.literal}")
                commands.registrar().register(command.build())
            }
        }

    }

}