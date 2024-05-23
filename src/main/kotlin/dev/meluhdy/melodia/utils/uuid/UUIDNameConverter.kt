package dev.meluhdy.melodia.utils.uuid

import dev.meluhdy.melodia.manager.MelodiaObject
import kotlinx.serialization.Serializable
import java.util.*

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