package dev.meluhdy.melodia.utils

import dev.meluhdy.melodia.Melodia
import dev.meluhdy.melodia.MelodiaPlugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import java.util.Locale
import java.util.PropertyResourceBundle
import java.util.ResourceBundle

data class TranslationFolder(val folderName: String, val defaultLang: Locale)

@Suppress("ArrayInDataClass")
data class TranslatedString(val id: String, val args: Array<Any>)

internal class TranslationBundleControl(val plugin: MelodiaPlugin) : ResourceBundle.Control() {

    override fun toBundleName(baseName: String, locale: Locale): String = "${baseName.replace("/", ".")}.${locale.language.toString().lowercase()}"

    override fun getFallbackLocale(baseName: String, locale: Locale): Locale? = if (locale == plugin.translationFolder.defaultLang) null else plugin.translationFolder.defaultLang

    override fun newBundle(
        baseName: String,
        locale: Locale,
        format: String,
        loader: ClassLoader,
        reload: Boolean
    ): ResourceBundle? {
        val bundleName = toBundleName(baseName, locale)
        val resourceName = toResourceName(bundleName, "properties")

        val stream = plugin.getResource(resourceName) ?: return null
        return stream.use { PropertyResourceBundle(it) }
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

    fun getBundle(plugin: MelodiaPlugin, lang: Locale): ResourceBundle = ResourceBundle.getBundle(plugin.translationFolder.folderName, lang,
        TranslationBundleControl(plugin))

    private fun getTranslationString(plugin: MelodiaPlugin, id: String, lang: Locale): String = getBundle(plugin, lang).getString(id)

    private fun getTranslationComponent(plugin: MelodiaPlugin, id: String, lang: Locale): TextComponent = text(getTranslationString(plugin, id, lang))

    /**
     * Translates a message and returns a MiniMessage
     */
    fun translate(plugin: MelodiaPlugin, id: String, lang: Locale, vararg args: Any): String {
        Melodia.logger.debug("Translating ID $id in ${plugin::class.simpleName} into ${lang.language} with args ${args.joinToString(", ")}")
        val template = getTranslationString(plugin, id, lang)

        if (args.isEmpty()) return template

        val out = StringBuilder()
        var curr = 0

        for (i in 0..<args.size) {
            val placeholder = "{$i}"

            while (true) {
                val placeholderIndex = template.indexOf(placeholder, curr)
                if (placeholderIndex == -1) break

                if (placeholderIndex > curr) out.append(template.substring(curr, placeholderIndex))

                val value = args[i]

                if (value is Component) out.append(value.toMiniMessage())
                else if(value is TranslatedString) out.append(translate(plugin, value.id, lang, *value.args))
                else out.append(value.toString())

                curr = placeholderIndex + placeholder.length
            }

        }

        if (curr < template.length) out.append(template.substring(curr))

        return out.toString()

    }

    fun translateList(plugin: MelodiaPlugin, id: String, lang: Locale, vararg args: Any): ArrayList<String> {
        Melodia.logger.debug("Translating list in ${plugin::class.simpleName} into ${lang.language} with ID $id and args ${args.joinToString(", ")}")
        val out = arrayListOf<String>()
        val bundle = getBundle(plugin, lang)
        var index = 0

        while (true) {

            val key = "$id.$index"

            if (!bundle.containsKey(key)) break

            out.add(translate(plugin, key, lang, args))

            index++
        }

        return out
    }

}