package dev.meluhdy.melodia.utils

import dev.meluhdy.melodia.manager.MelodiaObject
import dev.meluhdy.melodia.manager.MelodiaSavableManager
import dev.meluhdy.melodia.melodiaInstance
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.io.File
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.io.path.Path

/**
 * The serializer used to save UUIDNameConverter objects.
 */
object UUIDNameConverterSerializer : KSerializer<UUIDNameConverter> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("UUIDNameConverter") {
        element<String>("uuid")
        element<String>("name")
        element<Long>("timeStamp")
    }

    override fun deserialize(decoder: Decoder): UUIDNameConverter {
        return decoder.decodeStructure(descriptor) {
            var uuid = UUID.randomUUID()
            var name = ""
            var time = 0L
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> uuid = UUID.fromString(decodeStringElement(descriptor, 0))
                    1 -> name = decodeStringElement(descriptor, 1)
                    2 -> time = decodeLongElement(descriptor, 2)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> throw IndexOutOfBoundsException("Unexpected Index: $index")
                }
            }
            UUIDNameConverter(uuid, name, time)
        }
    }

    override fun serialize(encoder: Encoder, value: UUIDNameConverter) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.uuid.toString())
            encodeStringElement(descriptor, 1, value.name)
            encodeLongElement(descriptor, 2, value.timestamp)
        }
    }
}

/**
 * This is the object used to cache conversions between UUIDs and Names.
 *
 * @param uuid The UUID of the player.
 * @param name The Name of the player.
 * @param timestamp When this was cached.
 */
@Serializable(with = UUIDNameConverterSerializer::class)
class UUIDNameConverter(uuid: UUID, val name: String, val timestamp: Long) : MelodiaObject(uuid) {

    /**
     * Checks if the timestamp is old enough to warrant a new check on the username.
     *
     * @return True if the timestamp is too old, else false.
     */
    fun isTimestampOld(): Boolean {
        return System.currentTimeMillis() - this.timestamp >= 1000 * 60 * 60 * 24 * 7
    }

}

/**
 * Utils dealing with Player UUIDs.
 */
object UUIDUtils : MelodiaSavableManager<UUIDNameConverter>(), Listener {

    private const val NAME_TO_UUID: String = "https://api.mojang.com/users/profiles/minecraft/%s"
    private const val UUID_TO_NAME: String = "https://api.mojang.com/user/profile/%s"

    init {
        Bukkit.getPluginManager().registerEvents(this, melodiaInstance)
    }

    /**
     * Automatically caches player names and UUIDs upon join.
     *
     * @param event The PlayerJoinEvent.
     */
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        add(UUIDNameConverter(event.player.uniqueId, event.player.name, System.currentTimeMillis()))
    }

    /**
     * Gets the file where the UUIDs are stored.
     *
     * @return A File object with the UUID json file path.
     */
    private fun getFile(): File {
        val out = Path(melodiaInstance.dataFolder.toString(), "uuids.json").toFile()
        if (!out.exists()) {
            out.createNewFile()
            out.writeText("[]")
        }
        return out
    }

    /**
     * Gets a UUIDNameConverter given a player name from the Mojang API.
     *
     * @param name The name of the player to look up.
     *
     * @return The UUIDNameConverter cache object with both the name and username in the correct case.
     */
    private fun getFromName(name: String) : UUIDNameConverter {
        val data = URL(String.format(NAME_TO_UUID, name.lowercase())).readText()
        val jsonData = Json.parseToJsonElement(data).jsonObject

        val uuid = UUID.fromString(jsonData["id"]!!.jsonPrimitive.content)
        val converter = UUIDNameConverter(uuid, jsonData["name"]!!.jsonPrimitive.content, System.currentTimeMillis())

        add(converter)

        return converter
    }

    /**
     * Converts a player name to a UUID.
     *
     * @param name The name of the player to look up.
     *
     * @return The UUID of the player with that name.
     */
    fun getUUID(name: String): UUID {
        return getOrCreate({ item -> item.name.lowercase() == name.lowercase() }) { getFromName(name) }
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
        val data = URL(String.format(UUID_TO_NAME, id.toString())).readText()
        val jsonData = Json.parseToJsonElement(data).jsonObject

        val name = jsonData["name"]!!.jsonPrimitive.content
        val converter = UUIDNameConverter(id, name, System.currentTimeMillis())

        add(converter)

        return converter
    }

    /**
     * Converts a player UUID to a name.
     *
     * @param id The UUID of the player to look up.
     *
     * @return The name of the player with that name, in the correct case.
     */
    fun getName(id: UUID): String {
        return getOrCreate(id) { getFromUUID(id) }
            .apply { if (isTimestampOld()) getFromUUID(id) }
            .name
    }

    override fun saveObject(obj: UUIDNameConverter) {}

    override fun save() {
        getFile().writeText(serializer.encodeToString<ArrayList<UUIDNameConverter>>(getAll()))
    }

    override fun load() {
        serializer.decodeFromString<ArrayList<UUIDNameConverter>>(getFile().readText()).forEach { item -> add(item) }
    }

}