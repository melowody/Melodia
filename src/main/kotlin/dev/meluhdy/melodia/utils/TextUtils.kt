package dev.meluhdy.melodia.utils

import dev.meluhdy.melodia.Melodia
import dev.meluhdy.melodia.MelodiaPlugin
import dev.meluhdy.melodia.listener.PromptListener
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.Locale
import java.util.PropertyResourceBundle
import java.util.ResourceBundle

data class TranslationFolder(val folderName: String, val defaultLang: Locale)

@Suppress("ArrayInDataClass")
data class TranslatedString(val id: String, val args: Array<Any>)

internal class TranslationBundleControl(val plugin: MelodiaPlugin) : ResourceBundle.Control() {

    override fun toBundleName(baseName: String, locale: Locale): String = if (locale == Locale.ROOT) baseName else "${baseName}_${locale.language.lowercase()}"

    override fun getFallbackLocale(baseName: String, locale: Locale): Locale? = if (locale == plugin.translationFolder.defaultLang) null else plugin.translationFolder.defaultLang

    override fun newBundle(
        baseName: String,
        locale: Locale,
        format: String,
        loader: ClassLoader,
        reload: Boolean
    ): ResourceBundle? {
        val resourceName = "$baseName/${locale.language.lowercase()}.properties"
        Melodia.melodiaInstance.logger.debug("Attempting to load bundle from: $resourceName w/ plugin $plugin")

        val stream = plugin.getResource(resourceName) ?: return null
        return stream.use { PropertyResourceBundle(it.reader(Charsets.UTF_8)) }
    }

}

fun String.fromMiniMessage(): Component = MiniMessage.miniMessage().deserialize(this)

fun Component.toMiniMessage(): String = MiniMessage.miniMessage().serialize(this)

/**
 * A collection of functions to deal with text and chat messages
 */
@Suppress("unused")
object TextUtils {

    /**
     * Colors a string using the legacy color codes.
     *
     * @param message The message to colorize.
     * @param identifier The identifier for the color codes. Default: &
     *
     * @return A TextComponent colored using the inputted color codes.
     */
    fun legacyToMiniMessage(message: String, identifier: Char = '&'): String = MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacy(identifier).deserialize(message))

    fun getBundle(plugin: MelodiaPlugin, lang: Locale): ResourceBundle = ResourceBundle.getBundle(plugin.translationFolder.folderName, lang, plugin::class.java.classLoader, TranslationBundleControl(plugin))

    private fun getTranslationString(plugin: MelodiaPlugin, id: String, lang: Locale): String = getBundle(plugin, lang).getString(id)

    private fun getTranslationComponent(plugin: MelodiaPlugin, id: String, lang: Locale): TextComponent = text(getTranslationString(plugin, id, lang))

    /**
     * Translates a message and returns a MiniMessage
     */
    fun translate(plugin: MelodiaPlugin, id: String, lang: Locale, vararg args: Any): String {
        Melodia.melodiaInstance.logger.debug("Translating ID $id in ${plugin::class.simpleName} into ${lang.language} with args ${args.joinToString(", ")}")
        val template = getTranslationString(plugin, id, lang)

        if (args.isEmpty()) return template

        val regex = Regex("\\{(\\d+)}")

        return regex.replace(template) { result ->
            val index = result.groupValues[1].toInt()

            if (index >= args.size) return@replace result.value

            when (val value = args[index]) {
                is Component -> value.toMiniMessage()
                is TranslatedString -> translate(plugin, value.id, lang, *value.args)
                else -> value.toString()
            }
        }

    }

    fun translateList(plugin: MelodiaPlugin, id: String, lang: Locale, vararg args: Any): ArrayList<String> {
        Melodia.melodiaInstance.logger.debug("Translating list in ${plugin::class.simpleName} into ${lang.language} with ID $id and args ${args.joinToString(", ")}")
        val out = arrayListOf<String>()
        val bundle = getBundle(plugin, lang)
        var index = 0

        while (true) {

            val key = "$id.$index"

            if (!bundle.containsKey(key)) break

            out.add(translate(plugin, key, lang, *args))

            index++
        }

        return out
    }

    fun prompt(message: TextComponent, player: Player, callback: (TextComponent) -> Unit) {
        player.sendMessage(message)
        PromptListener.prompts[player.uniqueId] = callback
    }

    fun broadcastChat(plugin: MelodiaPlugin, stringId: String, vararg args: Any) {
        Bukkit.getOnlinePlayers().forEach { player ->
            player.sendMessage { legacyToMiniMessage(translate(plugin, stringId, player.locale(), *args)).fromMiniMessage() }
        }
    }

}