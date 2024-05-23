package dev.meluhdy.melodia.utils

import org.bukkit.Bukkit

@Suppress("unused")
object ConsoleUtils {

    enum class Color(private val string: String) {

        BLACK("\u001b[30m"),
        DARK_BLUE("\u001b[34m"),
        DARK_GREEN("\u001b[32m"),
        DARK_AQUA("\u001b[35m"),
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

        override fun toString(): String = string

    }

    enum class ConsoleLevel {
        INFO,
        DEBUG,
        VERBOSE
    }

    object ConsoleLogger {

        private const val PREFIX_FORMAT = "[ScovillePlugin/%s]"
        private val CONSOLE_LEVEL = ConsoleLevel.VERBOSE

        fun info(msg: String) {
            Bukkit.getConsoleSender().sendMessage("${PREFIX_FORMAT.format("INFO")} $msg${Color.RESET}")
        }

        fun debug(msg: String) {
            if (CONSOLE_LEVEL != ConsoleLevel.DEBUG && CONSOLE_LEVEL != ConsoleLevel.VERBOSE) return
            Bukkit.getConsoleSender().sendMessage("${Color.YELLOW}${PREFIX_FORMAT.format("DEBUG")} ${Color.RESET}$msg${Color.RESET}")
        }

        fun verbose(msg: String) {
            if (CONSOLE_LEVEL != ConsoleLevel.VERBOSE) return
            Bukkit.getConsoleSender().sendMessage("${Color.AQUA}${PREFIX_FORMAT.format("VERBOSE")} ${Color.RESET}$msg${Color.RESET}")
        }

    }

}

inline fun silence(body: () -> Unit) {
    try {
        body()
    } catch (_: Exception) {}
}