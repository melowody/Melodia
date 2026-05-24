package dev.meluhdy.melodia.manager

import dev.meluhdy.melodia.Melodia
import dev.meluhdy.melodia.utils.FileUtils
import dev.meluhdy.melodia.utils.toIsoString
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.Date

/**
 * An extension of MelodiaManager with file saving capabilities
 */
@Suppress("unused")
abstract class MelodiaSavingManager<T: MelodiaItem> : MelodiaManager<T>() {

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        val serializer = Json { prettyPrint = true; prettyPrintIndent = "\t"; allowTrailingComma = true; ignoreUnknownKeys = true }
    }

    open val savingObjects
        get() = objects

    /**
     * Saves the objects to individual files
     */
    open fun save() {
        synchronized(objects) {
            val uuids = savingObjects.map { it.uuid }.toSet()
            loadSaves().forEach {
                try {
                    val uuid = deserializeObject(serializer.decodeFromString<JsonElement>(it.readText())).uuid
                    if (uuid !in uuids) it.delete()
                } catch (e: Exception) {
                    Melodia.melodiaInstance.logger.error("Could not process file ${it.name}", e)
                }
            }
            ArrayList(savingObjects).forEach {
                if (!shouldSave(it)) return@forEach
                Melodia.melodiaInstance.logger.trace("Saving ${it.uuid} in Manager ${this::class.simpleName}")
                try {
                    val file = getFile(it)
                    Files.createDirectories(file.parentFile.toPath())
                    if (!file.exists()) file.createNewFile()

                    val temp = File(file.parentFile, "${file.name}.tmp")
                    temp.writeText(serializer.encodeToString(serializeObject(it)))
                    Files.move(temp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
                } catch (e: IOException) {
                    Melodia.melodiaInstance.logger.error("Could not save object ${it.uuid} in ${this.javaClass.simpleName}", e)
                }
            }
        }
    }

    /**
     * Loads all the files into objects
     */
    open fun load() {
        synchronized(objects) {
            loadSaves().forEach {
                Melodia.melodiaInstance.logger.trace("Loading item ${it.name} in Manager ${this::class.simpleName}")
                try {
                    add(deserializeObject(serializer.decodeFromString<JsonElement>(it.readText())))
                } catch (e: SerializationException) {
                    Melodia.melodiaInstance.logger.error("Could not load item ${it.name} in ${this.javaClass.simpleName}", e)
                    it.renameTo(FileUtils.getFile(it.parentFile, ".corrupted", "${it.name}.${Date().toIsoString()}"))
                }
            }
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