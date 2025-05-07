package dev.meluhdy.melodia

import dev.meluhdy.melodia.command.MelodiaCommand
import dev.meluhdy.melodia.manager.MelodiaSavingManager
import dev.meluhdy.melodia.utils.ConsoleLogger
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
    abstract val melodiaCommands: Array<MelodiaCommand>

    /**
     * The list of files in resources to overwrite with the plugin version
     */
    abstract val resourceFiles: Array<String>

    /**
     * The list of Listeners for the plugin to listen to
     */
    abstract val listeners: Array<Listener>

    /**
     * The location of the translation files
     */
    abstract val translationFolder: TranslationFolder

    /**
     * The logger
     */
    abstract val logger: ConsoleLogger

    abstract val savingManagers: Array<MelodiaSavingManager<*>>

    @Suppress("UnstableApiUsage")
    override fun onEnable() {

        logger.trace("Registering Commands...")
        lifecycleManager.registerEventHandler(
            LifecycleEvents.COMMANDS
        ) { commands ->
            melodiaCommands.forEach { command ->
                logger.debug("Registering Command: ${command.literal}")
                command.register()
                commands.registrar().register(command.build())
            }
        }

        logger.trace("Registering Listeners...")
        listeners.forEach { listener ->
            logger.debug("Registering Listener: ${listener::class.simpleName}")
            Bukkit.getPluginManager().registerEvents(listener, this)
        }

        logger.trace("Saving Resource Files...")
        resourceFiles.forEach { file ->
            logger.debug("Saving File: $file")
            this.saveResource(file, true)
        }

        logger.trace("Loading data...")
        savingManagers.forEach { manager -> manager.load() }

        logger.info("${logger.prefix} started!")

    }

    override fun onDisable() {
        logger.trace("Saving data...")
        savingManagers.forEach { manager -> manager.save() }
    }

}