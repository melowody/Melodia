package dev.meluhdy.melodia.gui

import dev.meluhdy.melodia.melodiaInstance
import net.kyori.adventure.text.TextComponent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

/**
 * A wrapper for GUIs to make them easier to initialize and handle
 *
 * @param p The Player to open the GUI for
 */
abstract class MelodiaGUI(protected val p: Player): InventoryHolder {

    /**
     * The amount of rows for the GUI to have (9 slots wide)
     */
    abstract val rows: Int

    /**
     * The title of the GUI
     */
    abstract val title: TextComponent

    private var _inv: Inventory? = null

    /**
     * The Inventory that acts as the GUI. This is unable to be set, and is generated automatically
     */
    val inv: Inventory
    get() = run {
        if (_inv == null) { _inv = melodiaInstance.server.createInventory(this, rows * 9, title) }
        _inv!!
    }

    /**
     * A list of clickable items to put into the GUI
     */
    protected abstract val melodiaItems: ArrayList<MelodiaGUIItem>

    /**
     * Initializes the inventory and opens it for the given Player
     */
    fun open() {
        initializeItems()
        this.p.openInventory(this.inv)
    }

    /**
     * Initializes all the items in the GUI before being shown to the Player
     */
    protected fun initializeItems() {
        clearItems()
        extraItems()

        melodiaItems.forEach { item ->
            this.inv.addItem(item)
        }
    }

    /**
     * Any extra non-clickable items in the GUI
     */
    protected abstract fun extraItems()

    /**
     * Clears all the items in the GUI
     */
    protected fun clearItems() {
        for (i in 0..<this.inv.size) this.inv.setItem(i, ItemStack(Material.AIR))
    }

    /**
     * The function that actually takes in the InventoryClickEvent and runs the correct MelodiaGUIItem (if it exists).
     * If you override this function you MUST put `@EventHandler(priority = EventPriority.HIGH)` before the function is defined
     *
     * @param e The InventoryClickEvent passed by Bukkit
     */
    @EventHandler(priority = EventPriority.HIGH)
    protected fun handleClick(e: InventoryClickEvent) {
        if (e.clickedInventory != this.inv) return
        if (e.rawSlot < this.inv.size) {
            e.isCancelled = true
        }
        melodiaItems.firstOrNull { item -> item.position == e.rawSlot }?.clickFunc?.accept(e)
        onInventoryClick(e)
    }

    /**
     * Any other GUI logic to be run when the player clicks in the GUI
     *
     * @param e The InventoryClickEvent passed by Bukkit
     */
    protected abstract fun onInventoryClick(e: InventoryClickEvent)

}