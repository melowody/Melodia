package dev.meluhdy.melodia.gui

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

/**
 * An ItemStack wrapper for clickable, positioned items in a GUI.
 */
data class MelodiaGUIItem(val position: Int, val item: ItemStack, val clickFunc: Consumer<in InventoryClickEvent>)

/**
 * Add a MelodiaGUIItem to the given Inventory.
 *
 * @param item The MelodiaGUIItem to add.
 */
fun Inventory.addItem(item: MelodiaGUIItem) {
    this.setItem(item.position, item.item)
}