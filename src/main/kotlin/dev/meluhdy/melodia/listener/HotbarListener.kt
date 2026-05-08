package dev.meluhdy.melodia.listener

import dev.meluhdy.melodia.misc.hotbar.isHotbarItem
import dev.meluhdy.melodia.misc.hotbar.runFunction
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.EquipmentSlot

object HotbarListener : Listener {

    @EventHandler
    fun onPlayerUseItem(e: PlayerInteractEvent) {

        if (!(e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK)) return
        if (e.hand != EquipmentSlot.HAND) return

        val player = e.player
        player.inventory.itemInMainHand.runFunction(player)

        if (player.inventory.itemInMainHand.isHotbarItem()) {
            e.isCancelled = true
            return
        }

    }

    @EventHandler
    fun onPlace(e: BlockPlaceEvent) {

        e.itemInHand.runFunction(e.player)

        if (e.itemInHand.isHotbarItem()) e.isCancelled = true

    }

    @EventHandler
    fun onToss(e: PlayerDropItemEvent) {

        if (e.itemDrop.itemStack.isHotbarItem()) {
            e.isCancelled = true
        }

    }

    @EventHandler
    fun onMove(e: InventoryClickEvent) {

        if (e.whoClicked.gameMode == GameMode.CREATIVE) return

        if (e.currentItem?.isHotbarItem() == true || e.cursor.isHotbarItem()) {
            e.isCancelled = true
            return
        }

        if (e.click == ClickType.NUMBER_KEY) {
            val item = e.whoClicked.inventory.getItem(e.hotbarButton)
            if (item?.isHotbarItem() == true) e.isCancelled = true
        }

    }

    @EventHandler
    fun onDrag(e: InventoryDragEvent) {

        if (e.whoClicked.gameMode == GameMode.CREATIVE) return

        if (e.oldCursor.isHotbarItem() || e.rawSlots.any { e.view.getItem(it)?.isHotbarItem() == true }) {
            e.isCancelled = true
        }

    }

    @EventHandler
    fun onSwap(e: PlayerSwapHandItemsEvent) {

        if (e.mainHandItem.isHotbarItem() || e.offHandItem.isHotbarItem()) {
            e.isCancelled = true
        }

    }

}