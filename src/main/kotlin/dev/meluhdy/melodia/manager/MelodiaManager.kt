package dev.meluhdy.melodia.manager

import java.util.UUID

abstract class MelodiaManager<T : MelodiaObject> {

    private val objects: ArrayList<T> = ArrayList()

    fun add(t: T) {
        objects.removeIf { obj -> obj.uuid == t.uuid }
        objects.add(t)
    }

    fun get(predicate: (T) -> Boolean): T? {
        return objects.find(predicate)
    }

    fun get(uuid: UUID): T? {
        return get { obj -> obj.uuid == uuid }
    }

    fun getOrCreate(uuid: UUID, factory: () -> T): T {
        if (objects.none { obj -> obj.uuid == uuid }) add(factory())
        return get { obj -> obj.uuid == uuid }!!
    }

    fun getOrCreate(predicate: (T) -> Boolean, factory: () -> T) : T {
        if (objects.none(predicate)) add(factory())
        return get(predicate)!!
    }

    fun getAll(): ArrayList<T> {
        return ArrayList<T>().apply { addAll(objects) }
    }

}