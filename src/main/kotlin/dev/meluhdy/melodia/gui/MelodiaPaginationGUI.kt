package dev.meluhdy.melodia.gui

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class MelodiaPaginationGUI<T>(p: Player, val prevGUI: MelodiaGUI? = null): MelodiaGUI(p) {

    abstract val itemRows: Int

    private var page = 0

    abstract val prevItem: ItemStack

    abstract val nextItem: ItemStack

    abstract val objects: ArrayList<T>

    protected val objectsOnPage: List<T>
        get() = objects.subList(page * this.itemRows * 9, ((page + 1) * this.itemRows * 9).coerceAtMost(objects.size))

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
            val list = melodiaItems.toCollection(ArrayList())
            list.addAll(objectMelodiaItems)
            return list
        }

    abstract fun toItem(pos: Int, obj: T): MelodiaGUIItem

}