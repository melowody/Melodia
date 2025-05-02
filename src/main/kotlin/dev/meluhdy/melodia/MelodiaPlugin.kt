package dev.meluhdy.melodia

import dev.meluhdy.melodia.command.MelodiaCommand
import dev.meluhdy.melodia.utils.TranslationFolder
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

/**
 * The base plugin, made to handle MelodiaCommands well
 */
abstract class MelodiaPlugin : JavaPlugin() {

    /**
     * The list of MelodiaCommands for the plugin to listen to
     */
    abstract val melodiaCommands: ArrayList<MelodiaCommand>

    /**
     * The list of files in resources to overwrite with the plugin version
     */
    abstract val resourceFiles: ArrayList<String>

    /**
     * The list of Listeners for the plugin to listen to
     */
    abstract val listeners: ArrayList<Listener>

    /**
     * The location of the translation files
     */
    abstract val translationFolder: TranslationFolder

    @Suppress("UnstableApiUsage")
    override fun onEnable() {

        lifecycleManager.registerEventHandler(
            LifecycleEvents.COMMANDS
        ) { commands ->
            melodiaCommands.forEach { command ->
                println("Registering ${command.literal}")
                commands.registrar().register(command.build())
            }
        }

        listeners.forEach { listener ->
            Bukkit.getPluginManager().registerEvents(listener, this)
        }

        resourceFiles.forEach { file ->
            this.saveResource(file, true)
        }

    }

}