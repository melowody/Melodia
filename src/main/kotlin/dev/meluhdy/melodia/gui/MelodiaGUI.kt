package dev.meluhdy.melodia.gui

import dev.meluhdy.melodia.Melodia
import dev.meluhdy.melodia.utils.ChatUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

abstract class MelodiaGUI(rows: Int, title: String, p: Player) : Listener {

    val p: Player
    val inv: Inventory

    init {
        this.p = p
        this.inv = Bukkit.createInventory(null, rows * 9, ChatUtils.colorize(title))
        Bukkit.getPluginManager().registerEvents(this, Melodia.plugin!!)
    }

    fun openInventory() {
        initializeItems()
        this.p.openInventory(this.inv)
    }

    protected abstract fun initializeItems()

    protected fun clearItems() {
        for (i in 0..this.inv.size)
            this.inv.setItem(i, ItemStack(Material.AIR))
    }

    @EventHandler
    fun invClickHandler(e: InventoryClickEvent) {
        if (e.inventory != this.inv) return
        if (e.rawSlot < this.inv.size) e.isCancelled = true
        onInventoryClick(e)
    }

    protected abstract fun onInventoryClick(e: InventoryClickEvent)

}