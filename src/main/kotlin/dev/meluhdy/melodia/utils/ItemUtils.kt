package dev.meluhdy.melodia.utils

import com.destroystokyo.paper.profile.PlayerProfile
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.util.UUID

object ItemUtils {

    private fun getProfile(url: String) : PlayerProfile {
        val profile = Bukkit.createProfile(UUID.randomUUID())
        val textures = profile.textures
        val urlObject: URL
        try {
            urlObject = URI(url).toURL()
        } catch (e: MalformedURLException) {
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
    fun modifyItem(item: ItemStack, title: String? = null, vararg lore: String): ItemStack {
        val itemMeta = item.itemMeta
        if (title != null)
            itemMeta.displayName(TextUtils.colorize(title))
        itemMeta.lore(lore.map(TextUtils::colorize))
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
    fun createItem(mat: Material, count: Int = 1, title: String? = null, vararg lore: String): ItemStack
        = modifyItem(ItemStack(mat, count), title, *lore)

    /**
     * Creates a custom skull given a skin URL.
     *
     * @param skullUrl The URL for the skin to pull the skull from.
     * @param count The amount in the ItemStack.
     * @param title The title of the ItemStack.
     * @param lore The lore of the ItemStack.
     */
    fun createSkull(skullUrl: String, count: Int = 1, title: String? = null, vararg lore: String): ItemStack {
        val item = ItemStack(Material.PLAYER_HEAD, count)
        if (skullUrl.isEmpty()) return modifyItem(item, title, *lore)

        val profile = getProfile(skullUrl)
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
    fun createSkull(player: UUID, count: Int = 1, title: String? = null, vararg lore: String): ItemStack {
        val item = ItemStack(Material.PLAYER_HEAD, count)
        val itemMeta = item.itemMeta as SkullMeta
        itemMeta.owningPlayer = Bukkit.getOfflinePlayer(player)
        item.itemMeta = itemMeta
        return modifyItem(item, title, *lore)
    }

}