package dev.meluhdy.melodia.manager

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import java.io.File
import java.nio.file.Path

abstract class MelodiaListSavableManager<T : MelodiaObject> : MelodiaSavableManager<T>() {

    abstract val path: Path
    abstract val tSerializer: KSerializer<T>

    protected val file: File
        get() = run {
            val out = path.toFile()
            if (!out.exists()) {
                out.createNewFile()
                out.writeText("[]")
            }
            out
        }

    override fun saveObject(obj: T) {}

    override fun save() {
        file.writeText(serializer.encodeToString(ListSerializer(tSerializer), getAll()))
    }

    override fun load() {
        serializer.decodeFromString(ListSerializer(tSerializer), file.readText()).forEach { item ->
            add(
                item
            )
        }
    }

}