package dev.meluhdy.melodia.utils.uuid

import dev.meluhdy.melodia.manager.MelodiaListSavableManager
import dev.meluhdy.melodia.melodiaInstance
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.net.URL
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path

/**
 * Utils dealing with Player UUIDs.
 */
object UUIDManager : MelodiaListSavableManager<UUIDNameConverter>(), Listener {

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
    override val path: Path = Path(melodiaInstance.dataFolder.toString(), "uuids.json")
    override val tSerializer: KSerializer<UUIDNameConverter> = UUIDNameConverterSerializer

}