package dev.meluhdy.melodia.gui

import dev.meluhdy.melodia.pluginInstance
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

/**
 * A GUI wrapper class to make creating GUIs much easier
 */
abstract class MelodiaGUI(val rows: Int, val title: String, protected val p: Player) : Listener {

    @SuppressWarnings("WeakerAccess")
    protected val inv: Inventory = Bukkit.createInventory(null, rows * 9, ChatUtils.colorize(title))

    init {
        Bukkit.getPluginManager().registerEvents(this, pluginInstance)
    }

    /**
     * The function to generate the items to be shown in the inventory
     */
    protected abstract val melodiaItems: ArrayList<MelodiaGUIItem>

    fun openInventory() {
        initializeItems()
        this.p.openInventory(this.inv)
    }

    protected fun initializeItems() {
        clearItems()
        extraItems()

        for (item in melodiaItems) {
            this.inv.addItem(item)
        }
    }

    /**
     * Function to place extra non-functional items
     */
    protected abstract fun extraItems()

    @SuppressWarnings("WeakerAccess")
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
        melodiaItems.stream().filter { item -> item.position == e.rawSlot }.findFirst().ifPresent { item -> item.clickFunc.accept(e) }
        onInventoryClick(e)
    }

    /**
     * Function to run when a player clicks somewhere in the inventory that's not on a MelodiaGUIItem
     *
     * @param e The InventoryClickEvent
     */
    protected abstract fun onInventoryClick(e: InventoryClickEvent)

}