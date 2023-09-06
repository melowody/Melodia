package dev.meluhdy.melodia.utils

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.lang.reflect.Field
import java.util.Base64
import java.util.UUID
import java.util.stream.Collectors

object ItemUtils {

    private fun modifyItem(
        item: ItemStack,
        title: String? = null,
        vararg lore: String
    ): ItemStack {
        val itemMeta = item.itemMeta
        if (title != null)
            itemMeta.displayName(ChatUtils.colorize(title))
        itemMeta.lore(
            lore.toList()
                .stream()
                .map(ChatUtils::colorize)
                .collect(Collectors.toList())
        )
        item.itemMeta = itemMeta
        return item
    }

    fun createItem(
        mat: Material,
        count: Int = 1,
        title: String? = null,
        vararg lore: String
    ): ItemStack = modifyItem(ItemStack(mat, count), title, *lore)

    fun createSkull(
        skullUrl: String,
        count: Int = 1,
        title: String? = null,
        vararg lore: String
    ): ItemStack {

        val item = ItemStack(Material.PLAYER_HEAD, count)
        if (skullUrl.isEmpty()) return modifyItem(item, title, *lore)

        val itemMeta: SkullMeta = item.itemMeta as SkullMeta
        val profile = GameProfile(UUID.randomUUID(), null)
        val encodeData: ByteArray = Base64.getEncoder().encode("{textures:{SKIN:{url:\"$skullUrl\"}}}".encodeToByteArray())
        profile.properties.put("textures", Property("textures", String(encodeData)))

        val profileField: Field = itemMeta.javaClass.getDeclaredField("profile")
        profileField.isAccessible = true
        profileField.set(itemMeta, profile)

        item.itemMeta = itemMeta
        return modifyItem(item, title, *lore)

    }

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