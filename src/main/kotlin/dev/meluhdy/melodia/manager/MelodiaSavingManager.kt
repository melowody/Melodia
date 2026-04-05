package dev.meluhdy.melodia.manager

import dev.meluhdy.melodia.Melodia
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import java.io.File
import java.io.IOException
import java.nio.file.Files

/**
 * An extension of MelodiaManager with file saving capabilities
 */
abstract class MelodiaSavingManager<T: MelodiaItem> : MelodiaManager<T>() {

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        val serializer = Json { prettyPrint = true; prettyPrintIndent = "\t"; allowTrailingComma = true }
    }

    /**
     * Saves the objects to individual files
     */
    open fun save() {
        loadSaves().forEach {
            if (!this.exists(deserializeObject(serializer.decodeFromString<JsonElement>(it.readText())).uuid))
                it.delete()
        }
        getAll().forEach {
            if (!shouldSave(it)) return
            Melodia.melodiaInstance.logger.trace("Saving ${it.uuid} in Manager ${this::class.simpleName}")
            try {
                val file = getFile(it)
                Files.createDirectories(file.parentFile.toPath())
                if (!file.exists()) file.createNewFile()
                file.writeText(serializer.encodeToString(serializeObject(it)))
            } catch (e: IOException) {
                Melodia.melodiaInstance.logger.error("Could not save object ${it.uuid} in ${this.javaClass.simpleName}")
                Melodia.melodiaInstance.logger.error(e.stackTraceToString())
            }
        }
    }

    /**
     * Loads all the files into objects
     */
    open fun load() {
        loadSaves().forEach {
            Melodia.melodiaInstance.logger.trace("Loading item ${it.name} in Manager ${this::class.simpleName}")
            add(deserializeObject(serializer.decodeFromString<JsonElement>(it.readText())))
        }
    }

    /**
     * Gets the file location from the object
     *
     * @param obj The object to get the save file location from
     */
    abstract fun getFile(obj: T): File

    /**
     * Get a list of all save files
     */
    abstract fun loadSaves(): Array<File>

    /**
     * Serializes an object to a JsonElement
     *
     * @param obj The object to serialize
     */
    abstract fun serializeObject(obj: T): JsonElement

    /**
     * Deserializes an object from a JsonElement
     *
     * @param jsonElement The JsonElement to deserialize
     */
    abstract fun deserializeObject(jsonElement: JsonElement): T

    open fun shouldSave(item: T): Boolean = true

}