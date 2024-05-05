package dev.meluhdy.melodia.utils

import com.destroystokyo.paper.profile.PlayerProfile
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.net.MalformedURLException
import java.net.URL
import java.util.UUID
import java.util.stream.Collectors

/**
 * A collection of utils for modifying and creating ItemStacks
 */
@Suppress("unused")
object ItemUtils {

    private fun getProfile(url: String) : PlayerProfile {
        val profile = Bukkit.createProfile(UUID.randomUUID())
        val textures = profile.textures
        val urlObject: URL
        try {
            urlObject = URL(url)
        } catch (e: MalformedURLException) {
            throw RuntimeException("Invalid URL", e)
        }
        textures.skin = urlObject
        profile.setTextures(textures)
        return profile
    }

    /**
     * Modifies an existing Item to add different lore and a different name.
     *
     * @param item The ItemStack to modify
     * @param title The new title to give it
     * @param lore The new lore to give it, by lines
     *
     * @return The modified ItemStack
     */
    private fun modifyItem(
        item: ItemStack,
        title: String? = null,
        vararg lore: String
    ): ItemStack {
        val itemMeta = item.itemMeta
        if (title != null)
            itemMeta.displayName(ChatUtils.colorize(title))
        itemMeta.lore(lore.map(ChatUtils::colorize))
        item.itemMeta = itemMeta
        return item
    }

    /**
     * Creates an ItemStack with custom lore and a title from scratch.
     *
     * @param mat The Material for the ItemStack
     * @param count The amount in the ItemStack
     * @param title The title of the ItemStack
     * @param lore The lore of the ItemStack
     *
     * @return The custom generated ItemStack
     */
    @Suppress("unused")
    fun createItem(
        mat: Material,
        count: Int = 1,
        title: String? = null,
        vararg lore: String
    ): ItemStack = modifyItem(ItemStack(mat, count), title, *lore)

    /**
     * Creates a custom skull given a skin URL.
     *
     * @param skullUrl The URL for the skin to pull the skull from
     * @param count The amount in the ItemStack
     * @param title The title of the ItemStack
     * @param lore The lore of the ItemStack
     *
     * @return The custom generated skull
     */
    @Suppress("unused")
    fun createSkull(
        skullUrl: String,
        count: Int = 1,
        title: String? = null,
        vararg lore: String
    ): ItemStack {

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
     * @param player The UUID of the player to steal the skull of
     * @param count The amount in the ItemStack
     * @param title The title of the ItemStack
     * @param lore The lore of the ItemStack
     *
     * @return The custom generated skull
     */
    @Suppress("unused")
    fun createSkull(
        player: UUID,
        count: Int = 1,
        title: String? = null,
        vararg lore: String
    ): ItemStack {

        val item = ItemStack(Material.PLAYER_HEAD, count)
        val itemMeta: SkullMeta = item.itemMeta as SkullMeta
        itemMeta.owningPlayer = Bukkit.getOfflinePlayer(player)
        item.itemMeta = itemMeta
        return modifyItem(item, title, *lore)

    }

}