package dev.meluhdy.melodia.utils

import dev.meluhdy.melodia.manager.MelodiaObject
import dev.meluhdy.melodia.manager.MelodiaSavableManager
import dev.meluhdy.melodia.pluginInstance
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.io.path.Path

class UUIDNameConverter(uuid: UUID, val name: String, val timestamp: Long) : MelodiaObject(uuid) {

    fun isTimestampOld(): Boolean {
        return System.currentTimeMillis() - this.timestamp >= 1000 * 60 * 60 * 24 * 7
    }

}

object UUIDUtils : MelodiaSavableManager<UUIDNameConverter>() {

    private const val NAME_TO_UUID: String = "https://api.mojang.com/users/profiles/minecraft/%s"
    private const val UUID_TO_NAME: String = "https://api.mojang.com/user/profile/%s"

    private val file = Path(pluginInstance.dataFolder.toString(), "uuids.json").toFile()

    private fun getFromName(name: String) : UUIDNameConverter {
        val data = URL(String.format(NAME_TO_UUID, name.lowercase())).readText()
        val jsonData = Json.parseToJsonElement(data).jsonObject

        val uuid = UUID.fromString(jsonData["id"]!!.jsonPrimitive.content)
        val converter = UUIDNameConverter(uuid, jsonData["name"]!!.jsonPrimitive.content, System.currentTimeMillis())

        return converter
    }

    fun getUUID(name: String): UUID {
        return getOrCreate({ item -> item.name.lowercase() == name.lowercase() }) { getFromName(name) }
            .apply { if (isTimestampOld()) getFromName(name)  }
            .uuid
    }

    private fun getFromUUID(id: UUID): UUIDNameConverter {
        val data = URL(String.format(UUID_TO_NAME, id.toString())).readText()
        val jsonData = Json.parseToJsonElement(data).jsonObject

        val name = jsonData["name"]!!.jsonPrimitive.content
        val converter = UUIDNameConverter(id, name, System.currentTimeMillis())

        return converter
    }

    fun getName(id: UUID): String {
        return getOrCreate(id) { getFromUUID(id) }
            .apply { if (isTimestampOld()) getFromUUID(id) }
            .name
    }

    override fun saveObject(obj: UUIDNameConverter) {}

    override fun save() {
        file.writeText(serializer.encodeToString(getAll()))
    }

    override fun load() {
        serializer.decodeFromString<ArrayList<UUIDNameConverter>>(file.readText()).forEach { item -> add(item) }
    }

}