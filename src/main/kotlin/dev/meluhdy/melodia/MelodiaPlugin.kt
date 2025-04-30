package dev.meluhdy.melodia

import dev.meluhdy.melodia.command.MelodiaCommand
import dev.meluhdy.melodia.utils.TextUtils
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * The base plugin, made to handle MelodiaCommands well
 */
abstract class MelodiaPlugin : JavaPlugin() {

    /**
     * The list of MelodiaCommands for the plugin to listen to
     */
    abstract val melodiaCommands: ArrayList<MelodiaCommand>

    /**
     * The list of languages that this plugin localizes to
     */
    abstract val languageEnum: HashMap<TextUtils.Language, String>

    /**
     * The list of files in resources to overwrite with the plugin version
     */
    abstract val resourceFiles: ArrayList<String>

    override fun onEnable() {

        lifecycleManager.registerEventHandler(
            LifecycleEvents.COMMANDS
        ) { commands ->
            melodiaCommands.forEach { command ->
                println("Registering ${command.literal}")
                commands.registrar().register(command.build())
            }
        }

        resourceFiles.forEach { file ->
            this.saveResource(file, true)
        }

    }

}