package dev.meluhdy.melodia.gui

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

data class MelodiaGUIItem(val position: Int, val item: ItemStack, val clickFunc: Consumer<in InventoryClickEvent>)

fun Inventory.addItem(item: MelodiaGUIItem) {
    this.setItem(item.position, item.item)
}