package dev.meluhdy.melodia.gui

import dev.meluhdy.melodia.Melodia
import dev.meluhdy.melodia.MelodiaPlugin
import dev.meluhdy.melodia.utils.TextUtils
import dev.meluhdy.melodia.utils.TranslatedString
import dev.meluhdy.melodia.utils.fromMiniMessage
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass

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

    companion object {
        private var registered: ArrayList<KClass<out MelodiaGUI>> = arrayListOf()
    }

    init {
        if (!registered.contains(this::class)) {
            registered.add(this::class)
            Bukkit.getPluginManager().registerEvents(this, plugin)
        }
    }

    /**
     * A list of clickable items to put into the GUI
     */
    protected abstract val melodiaItems: ArrayList<MelodiaGUIItem>

    /**
     * Initializes the inventory and opens it for the given Player
     */
    fun open() {
        Melodia.melodiaInstance.logger.debug("${p.name} is opening ${this::class.simpleName}")
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
    fun handleClick(e: InventoryClickEvent) {
        if (e.clickedInventory == null || e.clickedInventory!!.holder == null || e.clickedInventory!!.holder!!::class != this::class) return
        Melodia.melodiaInstance.logger.debug("${p.name} clicked ${e.rawSlot} in ${this::class.simpleName}")
        if (e.rawSlot < this.inv.size) {
            e.isCancelled = true
        }
        melodiaItems.firstOrNull { item -> item.position == e.rawSlot }?.clickFunc?.accept(e)
        onInventoryClick(e)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun handleDrag(e: InventoryDragEvent) {
        if (e.inventory.holder == null || e.inventory.holder!!::class != this::class) return
        e.isCancelled = true
    }

    /**
     * Any other GUI logic to be run when the player clicks in the GUI
     *
     * @param e The InventoryClickEvent passed by Bukkit
     */
    protected abstract fun onInventoryClick(e: InventoryClickEvent)

    override fun getInventory(): Inventory = inv

}