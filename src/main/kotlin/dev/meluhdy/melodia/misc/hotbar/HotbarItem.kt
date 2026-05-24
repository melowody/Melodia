package dev.meluhdy.melodia.misc.hotbar

import dev.meluhdy.melodia.Melodia
import dev.meluhdy.melodia.utils.FileUtils
import dev.meluhdy.melodia.utils.FileUtils.requireString
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

fun ItemStack.isHotbarItem(): Boolean = this.itemMeta?.persistentDataContainer?.get(HotbarItem.key, PersistentDataType.STRING) != null

fun ItemStack.runFunction(player: Player) {
    val key = this.itemMeta?.persistentDataContainer?.get(HotbarItem.key, PersistentDataType.STRING) ?: return
    HotbarItem.registry[key]?.invoke(player)
}

abstract class HotbarItem : ItemStack {

    companion object {
        internal val key = NamespacedKey(Melodia.melodiaInstance, (Melodia.melodiaInstance.config as YamlConfiguration).requireString("hotbar_key"))
        internal val registry: MutableMap<String, (Player) -> Unit> = mutableMapOf()
    }

    constructor(item: ItemStack) : super(item) {
        val meta = this.itemMeta
        meta?.persistentDataContainer?.set(key, PersistentDataType.STRING, this::class.qualifiedName!!)
        this.itemMeta = meta
        registry[this::class.qualifiedName!!] = ::onClick
    }

    abstract fun onClick(p: Player)

}