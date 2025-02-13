package dev.meluhdy.melodia.gui

import dev.meluhdy.melodia.melodiaInstance
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
 * A GUI wrapper class to make creating GUIs much easier.
 *
 * @param p The player to open the GUI for.
 */
abstract class MelodiaGUI(protected val p: Player) : Listener {

    /**
     * The amount of rows for the GUI to have.
     */
    abstract val rows: Int

    /**
     * The title of the GUI (e.g. "&8Main Menu").
     */
    abstract val title: String

    private var _inv: Inventory? = null

    val inv: Inventory
        get() = run {
            if (_inv == null) { _inv = Bukkit.createInventory(null, rows * 9, ChatUtils.colorize(title)) }
            _inv!!
        }

    init {
        Bukkit.getPluginManager().registerEvents(this, melodiaInstance)
    }

    /**
     * The function to generate the items to be shown in the inventory.
     */
    protected abstract val melodiaItems: ArrayList<MelodiaGUIItem>

    /**
     * Opens the inventory for the given player.
     */
    fun openInventory() {
        initializeItems()
        this.p.openInventory(this.inv)
    }

    /**
     * Initializes the items when the inventory is opened.
     */
    protected fun initializeItems() {
        clearItems()
        extraItems()

        for (item in melodiaItems) {
            this.inv.addItem(item)
        }
    }

    /**
     * Function to place extra non-functional items.
     * These are loaded *before* the MelodiaGUIItems are, and will be overwritten.
     */
    protected abstract fun extraItems()

    /**
     * Clears the items in the GUI.
     */
    @SuppressWarnings("WeakerAccess")
    protected fun clearItems() {
        for (i in 0..<this.inv.size)
            this.inv.setItem(i, ItemStack(Material.AIR))
    }

    /**
     * The logic for handling events in the GUI, the default call by Bukkit/Spigot.
     *
     * @param e The InventoryClickEvent.
     */
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
     * Function to run when a player clicks somewhere in the inventory that's not on a MelodiaGUIItem.
     *
     * @param e The InventoryClickEvent.
     */
    protected abstract fun onInventoryClick(e: InventoryClickEvent)

}