package dev.meluhdy.melodia.utils

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * Checks if an ItemStack is empty.
 */
@Suppress("unused")
fun ItemStack.isNone(): Boolean {
    return this.type == Material.AIR || this.amount == 0
}