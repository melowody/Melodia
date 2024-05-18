package dev.meluhdy.melodia.manager

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

/**
 * An exception to throw when an item could not be saved to a file.
 */
internal class CouldNotSaveException(msg: String) : Exception(msg)

/**
 * An upgrade to the MelodiaManager that tracks items and saves the states between server restarts.
 */
abstract class MelodiaSavableManager<T : MelodiaObject> : MelodiaManager<T>() {

    /**
     * The serializer used to write the objects to their files.
     */
    @OptIn(ExperimentalSerializationApi::class)
    protected val serializer: Json = Json { prettyPrint = true; prettyPrintIndent = "\t"; allowTrailingComma = true }

    /**
     * The default saving function to save items to individual files in their own folder.
     */
    open fun save() {
        getAll().forEach {
            obj -> run {
                try {
                    saveObject(obj)
                } catch (e: Exception) {
                    throw CouldNotSaveException("Could not save object ${obj.uuid} in ${this.javaClass.simpleName}")
                }
            }
        }
    }

    /**
     * The function to run to save one object to a file.
     *
     * @param obj The object to save.
     */
    abstract fun saveObject(obj: T)

    /**
     * The function to run to load all objects from their files.
     */
    abstract fun load()

}