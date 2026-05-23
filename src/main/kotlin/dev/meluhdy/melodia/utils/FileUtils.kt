package dev.meluhdy.melodia.utils

import dev.meluhdy.melodia.MelodiaPlugin
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
        var path = base.toPath()
        for (file in filePath) {
            path = path.resolve(file)
        }
        path = path.normalize()
        if (!path.startsWith(base.absolutePath)) throw InvalidParameterException("Attempted path traversal: $filePath")
        return path.toFile()
    }

}