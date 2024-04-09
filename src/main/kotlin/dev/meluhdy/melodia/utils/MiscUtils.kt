package dev.meluhdy.melodia.utils

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

fun ItemStack.isNone(): Boolean {
    return this.type == Material.AIR
}