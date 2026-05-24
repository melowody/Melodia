package dev.meluhdy.melodia.utils

import dev.meluhdy.melodia.MelodiaPlugin
import org.bukkit.Color
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import java.security.InvalidParameterException
import java.util.Properties
import kotlin.jvm.Throws

@Suppress("unused")
object FileUtils {

    @Throws(IOException::class)
    fun getYMLConfig(plugin: MelodiaPlugin, vararg filePath: String): YamlConfiguration {
        val file = getFile(plugin, *filePath)
        if (!file.exists()) file.createNewFile()
        assert(file.exists())
        return YamlConfiguration.loadConfiguration(file)
    }

    fun getProperties(plugin: MelodiaPlugin, vararg filePath: String): Properties {
        val file = getFile(plugin, *filePath)
        val properties = Properties()
        properties.load(file.inputStream())
        return properties
    }

    fun getFile(plugin: MelodiaPlugin, vararg filePath: String): File = getFile(plugin.dataFolder, *filePath)

    fun getFile(base: File, vararg filePath: String): File {
        val basePath = base.toPath()
        var path = basePath
        for (file in filePath) {
            path = path.resolve(file)
        }
        path = path.normalize()
        if (!path.startsWith(basePath)) throw InvalidParameterException("Attempted path traversal: ${filePath.joinToString(File.separator)}")
        return path.toFile()
    }

    fun YamlConfiguration.requireString(path: String): String = this.getString(path) ?: throw InvalidParameterException("Could not find key: $path")

    fun YamlConfiguration.requireStringList(path: String): List<String> {
        if (!this.contains(path)) throw InvalidParameterException("Could not find key: $path")
        return this.getStringList(path)
    }

    fun YamlConfiguration.requireColor(path: String): Color = this.getColor(path) ?: throw InvalidParameterException("Could not find key: $path")

    fun YamlConfiguration.requireInt(path: String): Int {
        if (!this.contains(path)) throw InvalidParameterException("Could not find key: $path")
        return this.getInt(path)
    }

}