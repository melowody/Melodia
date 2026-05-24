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

    override fun onEnable() {

        try {
            this.setupErrorHandler()
            try {
                this.saveDefaultConfig()
            } catch (_: IllegalArgumentException) {}

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
        } catch (e: Throwable) {
            logger.error(e = e)
            throw e
        }

    }

    override fun onDisable() {
        try {
            logger.trace("Saving data...")
            savingManagers.forEach { manager -> manager.save() }
        } catch (e: Throwable) {
            logger.error(e = e)
            throw e
        }
    }

    private fun setupErrorHandler() {
        val originalHandler = Thread.getDefaultUncaughtExceptionHandler()
        val pack = this::class.java.packageName

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            if (!checkIsPlugin(throwable, pack)) {
                originalHandler?.uncaughtException(thread, throwable)
                return@setDefaultUncaughtExceptionHandler
            }

            this.logger.error(e = throwable)
        }
    }

    private fun checkIsPlugin(throwable: Throwable?, pack: String): Boolean {
        if (throwable == null) return false
        val isThisPlugin = throwable.stackTrace.any { it.className.startsWith(pack) }
        return isThisPlugin || checkIsPlugin(throwable.cause, pack)
    }

}