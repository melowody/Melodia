package dev.meluhdy.melodia.utils

import dev.meluhdy.melodia.utils.LoggingUtils.ConsoleLevel
import org.bukkit.Bukkit

@Suppress("unused")
object LoggingUtils {

    enum class Color(val code: String) {

        BLACK("\u001b[30m"),
        DARK_BLUE("\u001b[34m"),
        DARK_GREEN("\u001b[32m"),
        DARK_AQUA("\u001b[36m"),
        DARK_RED("\u001b[31m"),
        DARK_PURPLE("\u001b[35m"),
        GOLD("\u001b[33m"),
        GRAY("\u001b[37m"),
        DARK_GRAY("\u001b[90m"),
        BLUE("\u001b[94m"),
        GREEN("\u001b[92m"),
        AQUA("\u001b[96m"),
        RED("\u001b[91m"),
        LIGHT_PURPLE("\u001b[95m"),
        YELLOW("\u001b[93m"),
        WHITE("\u001b[97m"),
        RESET("\u001b[0m");

        override fun toString(): String = code

    }

    enum class Style(val code: String) {
        BOLD("\u001b[1m"),
        DIM("\u001b[2m"),
        ITALIC("\u001b[3m"),
        UNDERLINE("\u001b[4m"),
        BLINK("\u001b[5m"),
        REVERSE("\u001b[6m"),
        HIDDEN("\u001b[7m"),
        STRIKE("\u001b[8m");

        override fun toString(): String = code
    }

    enum class ConsoleLevel(val color: Color, val level: Int) {
        INFO(Color.WHITE, 0),
        TRACE(Color.AQUA, 1),
        DEBUG(Color.LIGHT_PURPLE, 2),
        ERROR(Color.RED, 0);
    }

}

class ConsoleLogger(val prefix: String, val level: ConsoleLevel) {

    private val prefixFormat = "[$prefix/%s]"

    internal fun writeMessage(l: ConsoleLevel, m: String) {
        if (l.level > level.level) return
        Bukkit.getConsoleSender().sendMessage("${l.color}${prefixFormat.format(l.name)} $m${LoggingUtils.Color.RESET}")
    }

    fun info(msg: String) = writeMessage(ConsoleLevel.INFO, msg)

    fun error(msg: String, e: Exception) {
        writeMessage(ConsoleLevel.ERROR, msg)
        writeMessage(ConsoleLevel.ERROR, e.stackTraceToString())
    }

    fun trace(msg: String) = writeMessage(ConsoleLevel.TRACE, msg)

    fun debug(msg: String) = writeMessage(ConsoleLevel.DEBUG, msg)

}