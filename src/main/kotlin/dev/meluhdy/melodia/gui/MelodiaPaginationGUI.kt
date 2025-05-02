package dev.meluhdy.melodia.gui

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * An extension of MelodiaGUI to add pagination logic
 *
 * @param p The player to open the GUI for
 * @param prevGUI The GUI that opened this GUI, if it exists (Optional)
 */
@Suppress("unused")
abstract class MelodiaPaginationGUI<T>(p: Player, val prevGUI: MelodiaGUI? = null): MelodiaGUI(p) {

    /**
     * The number of rows for the items to appear in
     */
    abstract val itemRows: Int

    private var page = 0

    /**
     * The ItemStack that, when clicked, goes to the previous page in the GUI
     */
    abstract val prevItem: ItemStack

    /**
     * The ItemStack that, when clicked, goes to the next page in the GUI
     */
    abstract val nextItem: ItemStack

    /**
     * The list of all objects to be paginated through in the GUI
     */
    abstract val objects: ArrayList<T>

    /**
     * The list of objects on the current page
     */
    protected val objectsOnPage: List<T>
        get() = objects.subList(page * this.itemRows * 9, ((page + 1) * this.itemRows * 9).coerceAtMost(objects.size))

    /**
     * The list of all clickable objects on the current page
     */
    protected val objectMelodiaItems: ArrayList<MelodiaGUIItem>
        get() {
            val list: ArrayList<MelodiaGUIItem> = objectsOnPage.withIndex()
                .map { (index, item) -> toItem(index, item) }.toCollection(ArrayList())

            if (this.page == 0)
                list.add(MelodiaGUIItem(
                    (rows - 1) * 9,
                    prevItem
                ) { prevGUI?.open() ?: p.closeInventory() })
            else
                list.add(MelodiaGUIItem(
                    (rows - 1) * 9,
                    prevItem
                ) { this.page--; this.initializeItems() })

            if ((this.page + 1) * this.itemRows * 9 < objects.size)
                list.add(MelodiaGUIItem(
                    rows * 9 - 1,
                    nextItem
                ) { this.page++; this.initializeItems() })

            return list
        }

    override val melodiaItems: ArrayList<MelodiaGUIItem>
        get() {
            val list = ArrayList(melodiaItems)
            list.addAll(objectMelodiaItems)
            return list
        }

    /**
     * The function that converts an object in `objects` to a MelodiaGUIItem
     *
     * @param pos The position of the object in the list (not necessarily on the page!)
     * @param obj The object to be converted to a MelodiaGUIItem
     */
    abstract fun toItem(pos: Int, obj: T): MelodiaGUIItem

}