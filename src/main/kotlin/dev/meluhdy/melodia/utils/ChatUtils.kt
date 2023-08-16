package dev.meluhdy.melodia.utils

import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

object ChatUtils {

    fun colorize(message: String, identifier: Char = '&'): TextComponent {
        return LegacyComponentSerializer.legacy(identifier).deserialize(message)
    }

}