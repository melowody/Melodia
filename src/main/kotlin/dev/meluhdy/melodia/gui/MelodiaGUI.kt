package dev.meluhdy.melodia.gui

import dev.meluhdy.melodia.Melodia
import dev.meluhdy.melodia.MelodiaPlugin
import net.kyori.adventure.text.TextComponent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

/**
 * A wrapper for GUIs to make them easier to initialize and handle
 *
 * @param p The Player to open the GUI for
 */
abstract class MelodiaGUI(val plugin: MelodiaPlugin, val p: Player, val prevGUI: MelodiaGUI? = null): InventoryHolder, Listener {

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
        if (_inv == null) { _inv = Melodia.melodiaInstance.server.createInventory(this, rows * 9, title) }
        _inv!!
    }

    /**
     * A list of clickable items to put into the GUI
     */
    abstract val melodiaItems: ArrayList<MelodiaGUIItem>

    /**
     * Initializes the inventory and opens it for the given Player
     */
    open fun open() {
        Melodia.melodiaInstance.logger.debug("${p.name} is opening ${this::class.simpleName}")
        this.initializeItems()
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
     * Any other GUI logic to be run when the player clicks in the GUI
     *
     * @param e The InventoryClickEvent passed by Bukkit
     */
    abstract fun onInventoryClick(e: InventoryClickEvent)

    override fun getInventory(): Inventory = inv

}