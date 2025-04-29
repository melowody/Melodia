package dev.meluhdy.melodia.manager

import java.util.UUID

abstract class MelodiaManager<T: MelodiaItem> {

    private val objects: ArrayList<T> = arrayListOf()

    fun add(t: T) {
        delete(t.uuid)
        objects.add(t)
    }

    fun get(predicate: (T) -> Boolean): T? = objects.firstOrNull(predicate)

    fun get(uuid: UUID): T? = get { it.uuid == uuid }

    fun getOrCreate(predicate: (T) -> Boolean, factory: () -> T): T {
        if (objects.none(predicate)) add(factory())
        return get(predicate)!!
    }

    fun getOrCreate(uuid: UUID, factory: () -> T): T = getOrCreate({ it.uuid == uuid }, factory)

    fun getAll(): ArrayList<T> = ArrayList(objects)

    fun upsert(predicate: (T) -> Boolean, factory: () -> T, update: (T) -> T): T {
        val obj: T = getOrCreate(predicate, factory)
        add(update(obj))
        return obj
    }

    fun upsert(uuid: UUID, factory: () -> T, update: (T) -> T): T = upsert({ it.uuid == uuid }, factory, update)

    fun delete(predicate: (T) -> Boolean) {
        objects.removeIf(predicate)
    }

    fun delete(uuid: UUID) = delete({ it.uuid == uuid })

}