package dev.meluhdy.melodia

import dev.meluhdy.melodia.command.MelodiaCommand
import dev.meluhdy.melodia.listener.GUIListener
import dev.meluhdy.melodia.listener.HotbarListener
import dev.meluhdy.melodia.listener.PromptListener
import dev.meluhdy.melodia.manager.MelodiaSavingManager
import dev.meluhdy.melodia.utils.ConsoleLogger
import dev.meluhdy.melodia.utils.LoggingUtils
import dev.meluhdy.melodia.utils.TranslationFolder
import dev.meluhdy.melodia.utils.manager.UUIDManager
import org.bukkit.event.Listener
import java.util.*

class Melodia : MelodiaPlugin() {

    override val melodiaCommands: Array<MelodiaCommand> = arrayOf()
    override val resourceFiles: Array<String> = arrayOf()
    override val listeners: Array<Listener> = arrayOf(
        UUIDManager,
        PromptListener,
        GUIListener,
        HotbarListener
    )
    override val translationFolder: TranslationFolder = TranslationFolder("", Locale.ENGLISH)
    override val logger: ConsoleLogger = ConsoleLogger("Melodia", LoggingUtils.ConsoleLevel.DEBUG)
    override val savingManagers: Array<MelodiaSavingManager<*>> = arrayOf(
        UUIDManager
    )

    companion object {
        internal lateinit var plugin: Melodia
    }

    init {
        plugin = this
    }

}
