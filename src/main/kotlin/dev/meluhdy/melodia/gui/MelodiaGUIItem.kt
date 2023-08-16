package dev.meluhdy.melodia.gui

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

data class MelodiaGUIItem(val position: Int, var item: ItemStack)

fun Inventory.addItem(item: MelodiaGUIItem) {
    this.setItem(item.position, item.item)
}