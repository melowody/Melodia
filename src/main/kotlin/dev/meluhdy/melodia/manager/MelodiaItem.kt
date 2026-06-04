package dev.meluhdy.melodia.manager

import java.util.*

/**
 * A base class for items to be used in MelodiaManager
 */
abstract class MelodiaItem(val uuid: UUID = UUID.randomUUID()) {

    override fun equals(other: Any?): Boolean = other is MelodiaItem && uuid == other.uuid

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

}