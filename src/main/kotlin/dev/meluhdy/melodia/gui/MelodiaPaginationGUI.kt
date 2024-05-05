package dev.meluhdy.melodia.gui

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * The base for a Pagination GUI, Scoville-style.
 *
 * @param rows The amount of rows for the GUI in total
 * @param title The title of the GUI
 * @param p The player the GUI is paired with
 * @param prevGUI The previous GUI the player was on
 * @param itemRows
 */
@Suppress("unused")
abstract class MelodiaPaginationGUI<T>(rows: Int, title: String, p: Player, val prevGUI: MelodiaGUI?, val itemRows: Int): MelodiaGUI(rows, title, p) {

    private var page = 0

    /**
     * The ItemStack to use for the Previous Page button
     */
    abstract val prevItem: ItemStack
    /**
     * The ItemStack to use for the Back button
     */
    abstract val backItem: ItemStack

    /**
     * The ItemStack to use for the Next button
     */
    abstract val nextItem: ItemStack

    /**
     * The list of objects to paginate over
     */
    abstract val objects: ArrayList<T>

    override val melodiaItems: ArrayList<MelodiaGUIItem>
        get() {

        val amount = this.itemRows * 9
        val list: ArrayList<MelodiaGUIItem> = arrayListOf()

        for ((p, i) in objects.subList(page * amount, ((page + 1) * amount).coerceAtMost(objects.size)).withIndex()) {
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

        if ((page + 1) * amount < objects.size)
            list.add(MelodiaGUIItem(
                rows * 9 - 1,
                nextItem
            ) { this.page++
                this.initializeItems()
            })

        return list

    }

    /**
     * The function to convert the object to a GUI item
     *
     * @param pos The position of the item
     * @param obj The object to convert
     *
     * @return The GUI Item ready to place into the GUI
     */
    abstract fun toItem(pos: Int, obj: T): MelodiaGUIItem

}