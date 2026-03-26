package dev.meluhdy.melodia

import dev.meluhdy.melodia.command.MelodiaCommand
import dev.meluhdy.melodia.listener.PromptListener
import dev.meluhdy.melodia.manager.MelodiaSavingManager
import dev.meluhdy.melodia.utils.ConsoleLogger
import dev.meluhdy.melodia.utils.LoggingUtils
import dev.meluhdy.melodia.utils.TranslationFolder
import dev.meluhdy.melodia.utils.uuid.UUIDManager
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.Locale

class Melodia : MelodiaPlugin() {

    override val melodiaCommands: Array<MelodiaCommand> = arrayOf()
    override val resourceFiles: Array<String> = arrayOf()
    override val listeners: Array<Listener> = arrayOf(
        UUIDManager,
        PromptListener
    )
    override val translationFolder: TranslationFolder = TranslationFolder("", Locale.ENGLISH)
    override val logger: ConsoleLogger = ConsoleLogger("Melodia", LoggingUtils.ConsoleLevel.DEBUG)
    override val savingManagers: Array<MelodiaSavingManager<*>> = arrayOf(
        UUIDManager
    )

    companion object {
        internal lateinit var melodiaInstance: Melodia
    }

    override fun onEnable() {
        melodiaInstance = this

        super.onEnable()
    }

}
