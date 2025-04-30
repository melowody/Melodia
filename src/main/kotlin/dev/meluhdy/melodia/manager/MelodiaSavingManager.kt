package dev.meluhdy.melodia.manager

import dev.meluhdy.melodia.exception.CouldNotSaveException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import java.io.File
import java.io.IOException

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
    fun save() {
        getAll().forEach {
            try {
                val file = getFile(it)
                if (!file.exists()) file.createNewFile()
                file.writeText(serializer.encodeToString(serializeObject(it)))
            } catch (e: IOException) {
                throw CouldNotSaveException("Could not save object ${it.uuid} in ${this.javaClass.simpleName}")
                e.printStackTrace()
            }
        }
    }

    /**
     * Loads all the files into objects
     */
    fun load() {
        loadSaves().forEach {
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

}