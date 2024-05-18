package dev.meluhdy.melodia.gui

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * The base for a Pagination GUI, Scoville-style.
 *
 * @param p The player the GUI is paired with.
 * @param prevGUI The previous GUI the player was on.
 */
@Suppress("unused")
abstract class MelodiaPaginationGUI<T>(p: Player, val prevGUI: MelodiaGUI?): MelodiaGUI(p) {

    /**
     * The amount of rows for the paginated items to appear in.
     */
    abstract val itemRows: Int

    private var page = 0

    /**
     * The ItemStack to use for the Previous Page button.
     */
    abstract val prevItem: ItemStack
    /**
     * The ItemStack to use for the Back button.
     */
    abstract val backItem: ItemStack

    /**
     * The ItemStack to use for the Next button.
     */
    abstract val nextItem: ItemStack

    /**
     * The list of objects to paginate over.
     */
    abstract val objects: ArrayList<T>

    /**
     * The list of all objects on the current page.
     */
    protected val objectsOnPage: List<T>
        get() = objects.subList(page * this.itemRows * 9, ((page + 1) * this.itemRows * 9).coerceAtMost(objects.size))

    /**
     * The extra MelodiaGUIItems on the page needed for functionality.
     */
    abstract val extraMelodiaGUIItems: List<MelodiaGUIItem>

    override val melodiaItems: ArrayList<MelodiaGUIItem>
        get() {

        val list: ArrayList<MelodiaGUIItem> = arrayListOf()

        for ((p, i) in objectsOnPage.withIndex()) {
            list.add(toItem(p, i))
        }

        if (this.page == 0)
            list.add(MelodiaGUIItem(
                (rows - 1) * 9,
                backItem,
            ) { prevGUI?.openInventory() ?: p.closeInventory()
            })
        else
            list.add(MelodiaGUIItem(
                (rows - 1) * 9,
                prevItem
            ) { this.page--
                this.initializeItems()
            })

        if ((page + 1) * this.itemRows * 9 < objects.size)
            list.add(MelodiaGUIItem(
                rows * 9 - 1,
                nextItem
            ) { this.page++
                this.initializeItems()
            })

        list.addAll(extraMelodiaGUIItems)

        return list

    }

    /**
     * The function to convert the object to a GUI item.
     *
     * @param pos The position of the item.
     * @param obj The object to convert.
     *
     * @return The GUI Item ready to place into the GUI.
     */
    abstract fun toItem(pos: Int, obj: T): MelodiaGUIItem

}