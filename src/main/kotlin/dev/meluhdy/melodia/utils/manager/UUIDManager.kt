package dev.meluhdy.melodia.utils.manager

import dev.meluhdy.melodia.Melodia
import dev.meluhdy.melodia.manager.MelodiaItem
import dev.meluhdy.melodia.manager.MelodiaSavingManager
import dev.meluhdy.melodia.misc.serialization.MelodiaSerializer
import dev.meluhdy.melodia.misc.serialization.SerializerElement
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.io.File
import java.net.URI
import java.util.UUID

/**
 * This is the object used to cache conversions between UUIDs and Names.
 *
 * @param uuid The UUID of the player.
 * @param name The Name of the player.
 * @param timestamp When this was cached.
 */
class UUIDNameConverter(uuid: UUID, val name: String, val timestamp: Long): MelodiaItem(uuid) {

    /**
     * Checks if the timestamp is old enough to warrant a new check on the username.
     */
    fun isTimestampOld(): Boolean {
        return System.currentTimeMillis() - this.timestamp >= 1000 * 60 * 60 * 24 * 7
    }

}

object UUIDNameConverterSerializer: MelodiaSerializer<UUIDNameConverter>() {

    class UUIDNameConverterSerializerBuilder: Builder<UUIDNameConverter>() {

        lateinit var name: String
        var timestamp: Long = 0

        override fun build(): UUIDNameConverter = UUIDNameConverter(uuid, name, timestamp)

    }

    override val builder: Builder<UUIDNameConverter> = UUIDNameConverterSerializerBuilder()
    override val steps: Array<SerializerElement<*, UUIDNameConverter>> = arrayOf(
        SerializerElement<String, UUIDNameConverter>("name", String.serializer(), { it.name }, { string, builder -> (builder as UUIDNameConverterSerializerBuilder).name = string }),
        SerializerElement<Long, UUIDNameConverter>("timestamp", Long.serializer(), { it.timestamp }, { time, builder -> (builder as UUIDNameConverterSerializerBuilder).timestamp = time })
    )

}

object UUIDManager: MelodiaSavingManager<UUIDNameConverter>(), Listener {

    private const val NAME_TO_UUID: String = "https://api.mojang.com/users/profiles/minecraft/%s"
    private const val UUID_TO_NAME: String = "https://api.mojang.com/user/profile/%s"

    val baseFolder
        get() = "${Melodia.melodiaInstance.dataFolder.path}${File.separator}uuid"

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        add(UUIDNameConverter(event.player.uniqueId, event.player.name, System.currentTimeMillis()))
    }

    /**
     * Gets a UUIDNameConverter given a player name from the Mojang API.
     *
     * @param name The name of the player to look up.
     */
    private fun getFromName(name: String) : UUIDNameConverter {
        val data = URI.create(String.format(NAME_TO_UUID, name.lowercase())).toURL().readText()
        val jsonData = serializer.parseToJsonElement(data).jsonObject

        val uuid = UUID.fromString(jsonData["id"]!!.jsonPrimitive.content)
        val converter = UUIDNameConverter(uuid, jsonData["name"]!!.jsonPrimitive.content, System.currentTimeMillis())

        add(converter)

        return converter
    }

    /**
     * Converts a player name to a UUID.
     *
     * @param name The name of the player to look up.
     */
    fun getUUID(name: String): UUID {
        return getOrCreate({ item -> item.name.equals(name, ignoreCase = true) }) { getFromName(name) }
            .apply { if (isTimestampOld()) getFromName(name)  }
            .uuid
    }

    /**
     * Gets a UUIDNameConverter given a player UUID from the Mojang API.
     *
     * @param id The UUID of the player to look up.
     *
     * @return The UUIDNameConverter cache object with both the name and username in the correct case.
     */
    private fun getFromUUID(id: UUID): UUIDNameConverter {
        val data = URI.create(String.format(UUID_TO_NAME, id.toString())).toURL().readText()
        val jsonData = serializer.parseToJsonElement(data).jsonObject

        val name = jsonData["name"]!!.jsonPrimitive.content
        val converter = UUIDNameConverter(id, name, System.currentTimeMillis())

        add(converter)

        return converter
    }

    /**
     * Converts a player UUID to a name.
     *
     * @param id The UUID of the player to look up.
     */
    fun getName(id: UUID): String {
        return getOrCreate(id) { getFromUUID(id) }
            .apply { if (isTimestampOld()) getFromUUID(id) }
            .name
    }

    override fun getFile(obj: UUIDNameConverter): File = File(baseFolder, "${obj.uuid}.json")

    override fun loadSaves(): Array<File> = File(baseFolder).listFiles() ?: arrayOf()

    override fun serializeObject(obj: UUIDNameConverter): JsonElement = serializer.encodeToJsonElement(UUIDNameConverterSerializer, obj)

    override fun deserializeObject(jsonElement: JsonElement): UUIDNameConverter = serializer.decodeFromJsonElement(UUIDNameConverterSerializer, jsonElement)

}