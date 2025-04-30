package dev.meluhdy.melodia.gui

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

/**
 * An ItemStack wrapper to add functionality to individual ItemStacks upon click in a GUI
 *
 * @param position The position in the GUI for this ItemStack to be slotted into
 * @param item The ItemStack to display in the GUI
 * @param clickFunc The function to run when the ItemStack is clicked
 */
data class MelodiaGUIItem(val position: Int, val item: ItemStack, val clickFunc: Consumer<in InventoryClickEvent>)

/**
 * Adds the MelodiaGUIItem to the given Inventory
 *
 * @param item The MelodiaGUIItem to be added
 */
fun Inventory.addItem(item: MelodiaGUIItem) {
    this.setItem(item.position, item.item)
}