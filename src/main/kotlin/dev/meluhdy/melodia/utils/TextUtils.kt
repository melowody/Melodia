package dev.meluhdy.melodia.utils

import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

/**
 * A collection of functions to deal with text and chat messages
 */
object TextUtils {

    /**
     * Colors a string using the legacy color codes.
     *
     * @param message The message to colorize.
     * @param identifier The identifier for the color codes. Default: &
     *
     * @return A TextComponent colored using the inputted color codes.
     */
    fun legacyColorize(message: String, identifier: Char = '&'): TextComponent {
        return LegacyComponentSerializer.legacy(identifier).deserialize(message)
    }

}