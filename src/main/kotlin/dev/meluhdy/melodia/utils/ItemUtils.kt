package dev.meluhdy.melodia.utils

import com.destroystokyo.paper.profile.PlayerProfile
import dev.meluhdy.melodia.Melodia
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.security.InvalidParameterException
import java.util.UUID

object ItemUtils {

    private fun getProfile(url: URI) : PlayerProfile {
        Melodia.melodiaInstance.logger.debug("Getting Profile from Url: $url")
        val profile = Bukkit.createProfile(UUID.randomUUID())
        val textures = profile.textures
        val urlObject: URL
        try {
            urlObject = url.toURL()
        } catch (e: MalformedURLException) {
            Melodia.melodiaInstance.logger.error("Failed to load profile from Url: $url", e)
            throw RuntimeException("Invalid URL", e)
        }
        textures.skin = urlObject
        profile.setTextures(textures)
        return profile
    }

    /**
     * Modifies an existing ItemStack to change its lore and title
     *
     * @param item The ItemStack to modify
     * @param title The name to give it
     * @param lore The lore to give it, line by line
     */
    fun modifyItem(item: ItemStack, title: Component? = null, vararg lore: Component): ItemStack {
        Melodia.melodiaInstance.logger.debug("Modifying Item: ${item.type.name} with title $title and lore ${lore.joinToString(", ")}")
        val itemMeta = item.itemMeta
        if (title != null) itemMeta.displayName(title)
        itemMeta.lore(lore.toList())
        item.itemMeta = itemMeta
        return item
    }

    /**
     * Creates an ItemStack with custom lore and title
     *
     * @param mat The Material of the ItemStack
     * @param count The stack count of the ItemStack
     * @param title The title of the ItemStack
     * @param lore The lore to give it, line by line
     */
    fun createItem(mat: Material, count: Int = 1, title: Component? = null, vararg lore: Component): ItemStack
        = modifyItem(ItemStack(mat, count), title, *lore)

    /**
     * Creates a custom skull given a skin URL.
     *
     * @param skullUrl The URL for the skin to pull the skull from.
     * @param count The amount in the ItemStack.
     * @param title The title of the ItemStack.
     * @param lore The lore of the ItemStack.
     */
    fun createSkull(skullUrl: String, count: Int = 1, title: Component? = null, vararg lore: Component): ItemStack {
        Melodia.melodiaInstance.logger.debug("Creating $count skull${if (count != 1) 's' else ""} with URL $skullUrl, title $title, and lore ${lore.joinToString(", ")}")
        val item = ItemStack(Material.PLAYER_HEAD, count)
        if (skullUrl.isEmpty()) return modifyItem(item, title, *lore)

        if (!skullUrl.startsWith("https://textures.minecraft.net")) throw InvalidParameterException("Invalid URL")
        val profile = getProfile(URI(skullUrl))
        val meta = item.itemMeta as SkullMeta
        meta.playerProfile = profile
        item.itemMeta = meta

        return modifyItem(item, title, *lore)
    }

    /**
     * Creates a custom skull given a player UUID.
     *
     * @param player The UUID of the player to steal the skull of.
     * @param count The amount in the ItemStack.
     * @param title The title of the ItemStack.
     * @param lore The lore of the ItemStack.
     *
     */
    fun createSkull(player: UUID, count: Int = 1, title: Component? = null, vararg lore: Component): ItemStack {
        Melodia.melodiaInstance.logger.debug("Creating $count skull${if (count != 1) 's' else ""} with UUID $player, title $title, and lore ${lore.joinToString(", ")}")
        val item = ItemStack(Material.PLAYER_HEAD, count)
        val itemMeta = item.itemMeta as SkullMeta
        itemMeta.owningPlayer = Bukkit.getOfflinePlayer(player)
        item.itemMeta = itemMeta
        return modifyItem(item, title, *lore)
    }

}