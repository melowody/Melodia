package dev.meluhdy.melodia.manager

import java.util.UUID

/**
 * A class to manage MelodiaItems and keep track of them easily
 */
abstract class MelodiaManager<T: MelodiaItem> {

    private val objects: ArrayList<T> = arrayListOf()

    /**
     * Adds an object to the MelodiaManager.
     * This will check if the object exists and replace it if it does
     *
     * @param t The object to be added
     */
    fun add(t: T) {
        delete(t.uuid)
        objects.add(t)
    }

    /**
     * Gets the first object by the predicate, otherwise null
     *
     * @param predicate The predicate to select the item by
     */
    fun get(predicate: (T) -> Boolean): T? = objects.firstOrNull(predicate)

    /**
     * Gets an object by its UUID, otherwise null
     *
     * @param uuid The UUID of the object to get
     */
    fun get(uuid: UUID): T? = get { it.uuid == uuid }

    /**
     * Gets the first object by the predicate, otherwise creates one and adds it to the Manager
     *
     * @param predicate The predicate to select the object by
     * @param factory The generator for the object if it can't be found
     */
    fun getOrCreate(predicate: (T) -> Boolean, factory: () -> T): T {
        if (objects.none(predicate)) add(factory())
        return get(predicate)!!
    }

    /**
     * Gets the first object with the UUID, otherwise creates one and adds it to the Manager
     *
     * @param uuid The UUID of the object
     * @param factory The generator for the object if it can't be found
     */
    fun getOrCreate(uuid: UUID, factory: () -> T): T = getOrCreate({ it.uuid == uuid }, factory)

    /**
     * Gets all the objects in the Manager
     */
    fun getAll(): ArrayList<T> = ArrayList(objects)

    /**
     * Updates the first object that fits the predicate, otherwise adds an object and updates it
     *
     * @param predicate The predicate to select the object by
     * @param factory The generator for the object if it can't be found
     * @param update The update function that modifies the object
     */
    fun upsert(predicate: (T) -> Boolean, factory: () -> T, update: (T) -> T): T {
        val obj: T = getOrCreate(predicate, factory)
        add(update(obj))
        return obj
    }

    /**
     * Updates the first object with the given UUID, otherwise adds an object and updates it
     *
     * @param uuid The UUID of the object
     * @param factory The generator for the object if it can't be found
     * @param update The update function that modifies the object
     */
    fun upsert(uuid: UUID, factory: () -> T, update: (T) -> T): T = upsert({ it.uuid == uuid }, factory, update)

    /**
     * Deletes all objects that match the predicate
     *
     * @param predicate The predicate to filter the objects by
     */
    fun delete(predicate: (T) -> Boolean) {
        objects.removeIf(predicate)
    }

    /**
     * Deletes the object with the given UUID
     *
     * @param uuid The UUID of the object to delete
     */
    fun delete(uuid: UUID) = delete { it.uuid == uuid }

}