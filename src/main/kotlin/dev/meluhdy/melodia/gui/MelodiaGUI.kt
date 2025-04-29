package dev.meluhdy.melodia.gui

import dev.meluhdy.melodia.melodiaInstance
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

abstract class MelodiaGUI(protected val p: Player): InventoryHolder {

    abstract val rows: Int

    abstract val title: String

    private var _inv: Inventory? = null

    val inv: Inventory
    get() = run {
        if (_inv == null) { _inv = melodiaInstance.server.createInventory(this, rows * 9, Component.text(title)) }
        _inv!!
    }

    protected abstract val melodiaItems: ArrayList<MelodiaGUIItem>

    fun open() {
        initializeItems()
        this.p.openInventory(this.inv)
    }

    protected fun initializeItems() {
        clearItems()
        extraItems()

        melodiaItems.forEach { item ->
            this.inv.addItem(item)
        }
    }

    protected abstract fun extraItems()

    protected fun clearItems() {
        for (i in 0..<this.inv.size) this.inv.setItem(i, ItemStack(Material.AIR))
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun handleClick(e: InventoryClickEvent) {
        if (e.clickedInventory != this.inv) return
        if (e.rawSlot < this.inv.size) {
            e.isCancelled = true
        }
        melodiaItems.firstOrNull { item -> item.position == e.rawSlot }?.clickFunc?.accept(e)
        onInventoryClick(e)
    }

    protected abstract fun onInventoryClick(e: InventoryClickEvent)

}