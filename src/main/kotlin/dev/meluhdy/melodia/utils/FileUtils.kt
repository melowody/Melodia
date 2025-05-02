package dev.meluhdy.melodia.utils

import dev.meluhdy.melodia.MelodiaPlugin
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import java.util.Properties
import kotlin.jvm.Throws

@Suppress("unused")
object FileUtils {

    @Throws(IOException::class)
    fun getYMLConfig(plugin: MelodiaPlugin, fileName: String): YamlConfiguration {

        val file = File(plugin.dataFolder, fileName)
        if (!file.exists()) file.createNewFile()
        assert(file.exists())
        return YamlConfiguration.loadConfiguration(file)
    }

    fun getProperties(plugin: MelodiaPlugin, fileName: String): Properties {
        val file = File(plugin.dataFolder, fileName)
        val properties = Properties()
        properties.load(file.inputStream())
        return properties
    }

}