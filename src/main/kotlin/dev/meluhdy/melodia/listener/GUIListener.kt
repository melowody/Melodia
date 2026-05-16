package dev.meluhdy.melodia.listener

import dev.meluhdy.melodia.gui.MelodiaGUI
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent

object GUIListener : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onInventoryClick(e: InventoryClickEvent) {

        val holder = e.inventory.holder

        if (holder !is MelodiaGUI) return

        if (e.rawSlot < e.inventory.size) e.isCancelled = true
        if (e.clickedInventory == holder.inventory) {
            val item = holder.melodiaItems.firstOrNull { it.position == e.rawSlot }
            item?.clickFunc?.accept(e)
            holder.onInventoryClick(e)
            (e.whoClicked as Player).updateInventory()
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onInventoryDrag(e: InventoryDragEvent) {
        if (e.inventory.holder is MelodiaGUI) e.isCancelled = true
    }

}