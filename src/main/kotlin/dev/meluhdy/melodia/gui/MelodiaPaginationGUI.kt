package dev.meluhdy.melodia.gui

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class MelodiaPaginationGUI<T>(private val rows: Int, title: String, p: Player, val prevGUI: MelodiaGUI, private val itemRows: Int): MelodiaGUI(rows, title, p) {

    private var page = 0

    override fun getMelodiaItems(): ArrayList<MelodiaGUIItem> {

        val amount = this.itemRows * 9
        val list: ArrayList<MelodiaGUIItem> = arrayListOf()
        val objs: ArrayList<T> = getObjects()

        for ((p, i) in objs.subList(page * amount, ((page + 1) * amount).coerceAtMost(objs.size)).withIndex()) {
            list.add(toItem(p, i))
        }

        if (this.page == 0)
            list.add(MelodiaGUIItem(
                (rows - 1) * 9,
                getBackItem(),
            ) { prevGUI.openInventory()
            })
        else
            list.add(MelodiaGUIItem(
                (rows - 1) * 9,
                getPrevItem()
            ) { this.page--
                this.initializeItems()
            })

        if ((page + 1) * amount < objs.size)
            list.add(MelodiaGUIItem(
                rows * 9 - 1,
                getNextItem()
            ) { this.page++
                this.initializeItems()
            })

        return list

    }

    abstract fun getPrevItem(): ItemStack

    abstract fun getBackItem(): ItemStack

    abstract fun getNextItem(): ItemStack

    abstract fun toItem(pos: Int, obj: T): MelodiaGUIItem

    abstract fun getObjects(): ArrayList<T>

}