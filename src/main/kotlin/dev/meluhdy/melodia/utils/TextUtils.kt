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

fun String.legacyToMiniMessage(identifier: Char = '&'): String = TextUtils.mm.serialize(LegacyComponentSerializer.legacy(identifier).deserialize(this))

fun String.miniToLegacyMessage(identifier: Char = '&'): String = LegacyComponentSerializer.legacy(identifier).serialize(MiniMessage.miniMessage().deserialize(this))

fun String.fromMiniMessage(): Component = TextUtils.mm.deserialize(this)

fun Component.toMiniMessage(): String = TextUtils.mm.serialize(this)

fun String.fromLegacyMessage(identifier: Char = '&'): Component = LegacyComponentSerializer.legacy(identifier).deserialize(this)

fun Component.toLegacyMessage(identifier: Char = '&'): String = LegacyComponentSerializer.legacy(identifier).serialize(this)

/**
 * A collection of functions to deal with text and chat messages
 */
@Suppress("unused")
object TextUtils {

    internal val mm: MiniMessage = MiniMessage.miniMessage()

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

    fun getTranslatedStringList(plugin: MelodiaPlugin, id: String, lang: Locale, vararg args: Any): ArrayList<TranslatedString> {
        Melodia.melodiaInstance.logger.debug("Getting list of translation strings for id $id in plugin ${plugin::class.simpleName}")
        val out = arrayListOf<TranslatedString>()
        val bundle = getBundle(plugin, lang)
        var index = 0

        while (true) {

            val key = "$id.$index"

            if (!bundle.containsKey(key)) break

            out.add(TranslatedString(key, arrayOf(*args)))

            index++
        }

        return out
    }

    fun translateList(plugin: MelodiaPlugin, id: String, lang: Locale, vararg args: Any): List<String> {
        Melodia.melodiaInstance.logger.debug("Translating list in ${plugin::class.simpleName} into ${lang.language} with ID $id and args ${args.joinToString(", ")}")
        return this.getTranslatedStringList(plugin, id, lang, *args).map { ts -> translate(plugin, ts.id, lang, *ts.args) }
    }

    fun prompt(message: TextComponent, player: Player, callback: (TextComponent) -> Unit) {
        player.sendMessage(message)
        PromptListener.prompts[player.uniqueId] = callback
    }

    fun broadcastChat(plugin: MelodiaPlugin, stringId: String, vararg args: Any) {
        Bukkit.getOnlinePlayers().forEach { player ->
            player.sendMessage { translate(plugin, stringId, player.locale(), *args).fromLegacyMessage() }
        }
    }

    fun broadcastChat(component: Component) {
        Bukkit.getOnlinePlayers().forEach { player ->
            player.sendMessage(component)
        }
    }

    fun broadcastChat(string: String) = this.broadcastChat(string.fromMiniMessage())

}