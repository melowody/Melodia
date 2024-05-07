package dev.meluhdy.melodia.manager

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

internal class CouldNotSaveException(msg: String) : Exception(msg)

abstract class MelodiaSavableManager<T : MelodiaObject> : MelodiaManager<T>() {

    @OptIn(ExperimentalSerializationApi::class)
    protected val serializer: Json = Json { prettyPrint = true; prettyPrintIndent = "\t"; allowTrailingComma = true }

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

    abstract fun saveObject(obj: T)
    abstract fun load()

}