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
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

/**
 * A wrapper for GUIs to make them easier to initialize and handle
 *
 * @param p The Player to open the GUI for
 */
abstract class MelodiaGUI(val plugin: MelodiaPlugin, protected val p: Player): InventoryHolder, Listener {

    /**
     * The amount of rows for the GUI to have (9 slots wide)
     */
    abstract val rows: Int

    /**
     * The title of the GUI
     */
    abstract val titleID: TranslatedString

    private var _inv: Inventory? = null

    /**
     * The Inventory that acts as the GUI. This is unable to be set, and is generated automatically
     */
    val inv: Inventory
    get() = run {
        if (_inv == null) { _inv = Melodia.melodiaInstance.server.createInventory(this, rows * 9, TextUtils.translate(plugin, titleID.id, p.locale(), *titleID.args).fromMiniMessage()) }
        _inv!!
    }

    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    /**
     * A list of clickable items to put into the GUI
     */
    protected abstract val melodiaItems: ArrayList<MelodiaGUIItem>

    /**
     * Initializes the inventory and opens it for the given Player
     */
    fun open() {
        Melodia.logger.debug("${p.name} is opening ${this::class.simpleName}")
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
        Melodia.logger.debug("${p.name} clicked ${e.rawSlot} in ${this::class.simpleName}")
        Melodia.logger.debug("${e.clickedInventory!!.holder!!::class} <-> ${this::class}")
        if (e.rawSlot < this.inv.size) {
            e.isCancelled = true
        }
        if (e.clickedInventory == null || e.clickedInventory!!.holder == null || e.clickedInventory!!.holder!!::class != this::class) return
        melodiaItems.firstOrNull { item -> item.position == e.rawSlot }?.clickFunc?.accept(e)
        onInventoryClick(e)
    }

    /**
     * Any other GUI logic to be run when the player clicks in the GUI
     *
     * @param e The InventoryClickEvent passed by Bukkit
     */
    protected abstract fun onInventoryClick(e: InventoryClickEvent)

    override fun getInventory(): Inventory = inv

}