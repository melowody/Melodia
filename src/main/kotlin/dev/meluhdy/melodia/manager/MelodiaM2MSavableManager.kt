package dev.meluhdy.melodia.manager

import kotlinx.serialization.encodeToString
import java.io.File
import java.nio.file.Path

abstract class MelodiaM2MSavableManager<T : MelodiaObject> : MelodiaSavableManager<T>() {

    abstract val path: Path

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
        file.writeText(serializer.encodeToString<ArrayList<T>>(getAll()))
    }

    override fun load() {
        serializer.decodeFromString<ArrayList<T>>(file.readText()).forEach { item ->
            add(
                item
            )
        }
    }

}