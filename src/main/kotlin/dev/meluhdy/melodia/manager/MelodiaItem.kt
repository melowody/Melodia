package dev.meluhdy.melodia.manager

import java.util.UUID

abstract class MelodiaItem(val uuid: UUID = UUID.randomUUID()) {
    override fun equals(other: Any?): Boolean = other is MelodiaItem && uuid == other.uuid
}