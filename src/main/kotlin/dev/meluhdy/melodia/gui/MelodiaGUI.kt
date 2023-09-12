package dev.meluhdy.melodia.gui

import dev.meluhdy.melodia.Melodia
import dev.meluhdy.melodia.utils.ChatUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

abstract class MelodiaGUI(rows: Int, title: String, protected val p: Player) : Listener {

    protected val inv: Inventory

    init {
        this.inv = Bukkit.createInventory(null, rows * 9, ChatUtils.colorize(title))
        Bukkit.getPluginManager().registerEvents(this, Melodia.plugin!!)
    }

    protected abstract fun getMelodiaItems(): ArrayList<MelodiaGUIItem>

    fun openInventory() {
        initializeItems()
        this.p.openInventory(this.inv)
    }

    protected fun initializeItems() {
        clearItems()
        extraItems()

        for (item in getMelodiaItems()) {
            this.inv.addItem(item)
        }
    }

    protected abstract fun extraItems()

    protected fun clearItems() {
        for (i in 0..<this.inv.size)
            this.inv.setItem(i, ItemStack(Material.AIR))
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun handleClick(e: InventoryClickEvent) {
        if (e.inventory != this.inv) return
        if (e.rawSlot < this.inv.size) {
            e.isCancelled = true
        }
        getMelodiaItems().stream().filter { item -> item.position == e.rawSlot }.findFirst().ifPresent { item -> item.clickFunc.accept(e) }
        onInventoryClick(e)
    }

    protected abstract fun onInventoryClick(e: InventoryClickEvent)

}