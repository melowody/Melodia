package dev.meluhdy.melodia.manager

import java.util.UUID

/**
 * A base manager class for managing any item that needs to be tracked.
 * This does not save between server resets.
 */
abstract class MelodiaManager<T : MelodiaObject> {

    /**
     * The list of objects to keep track of.
     */
    private val objects: ArrayList<T> = ArrayList()

    /**
     * Adds an item to be tracked by the manager.
     *
     * @param t The item.
     */
    fun add(t: T) {
        objects.removeIf { obj -> obj.uuid == t.uuid }
        objects.add(t)
    }

    /**
     * Gets an item with the given predicate.
     *
     * @param predicate The deciding factor in which items get filtered out.
     *
     * @return The item, if found, else null.
     */
    fun get(predicate: (T) -> Boolean): T? {
        return objects.find(predicate)
    }

    /**
     * Gets an item with the given UUID.
     *
     * @param uuid The UUID of the item to get.
     *
     * @return The item, if found, else null.
     */
    fun get(uuid: UUID): T? {
        return get { obj -> obj.uuid == uuid }
    }

    /**
     * Gets an item with the given UUID, else creates one.
     *
     * @param uuid The UUID of the item to get.
     * @param factory The function to run if the item is not found.
     *
     * @return The item. This will never be null.
     */
    fun getOrCreate(uuid: UUID, factory: () -> T): T {
        if (objects.none { obj -> obj.uuid == uuid }) add(factory())
        return get { obj -> obj.uuid == uuid }!!
    }

    /**
     * Gets an item with the given predicate, else creates one.
     *
     * @param predicate The deciding factor in which items get filtered out.
     * @param factory The function to run if the item is not found.
     *
     * @return The item. This will never be null.
     */
    fun getOrCreate(predicate: (T) -> Boolean, factory: () -> T) : T {
        if (objects.none(predicate)) add(factory())
        return get(predicate)!!
    }

    /**
     * Gets a shallow copy of the list of objects, so as not to easily disturb the list the Manager uses to track with.
     *
     * @return The list of objects.
     */
    fun getAll(): ArrayList<T> {
        return ArrayList<T>().apply { addAll(objects) }
    }

    /**
     * Updates the object with the given predicate, otherwise creates one with the changes specified.
     *
     * @param predicate The deciding factor in which item to update.
     * @param factory If the item is not found, this generates the item.
     * @param update This modifies the item.
     *
     * @return The item.
     */
    fun upsert(predicate: (T) -> Boolean, factory: () -> T, update: (T) -> T) : T {
        val obj: T = getOrCreate(predicate, factory)
        add(update(obj))
        return obj
    }

    /**
     * Updates the object with the given predicate, otherwise creates one with the changes specified.
     *
     * @param uuid The UUID of the item
     * @param factory If the item is not found, this generates the item.
     * @param update This modifies the item.
     *
     * @return The item.
     */
    fun upsert(uuid: UUID, factory: () -> T, update: (T) -> T) : T {
        val obj: T = getOrCreate(uuid, factory)
        add(update(obj))
        return obj
    }

    /**
     * Removes an item from the manager's cache.
     *
     * @param predicate The deciding factory in which item(s) to remove.
     */
    fun delete(predicate: (T) -> Boolean) {
        objects.removeIf(predicate)
    }

    /**
     * Removes an item from the manager's cache.
     *
     * @param uuid The UUID of the item to remove.
     */
    fun delete(uuid: UUID) {
        delete { obj -> obj.uuid == uuid }
    }

}