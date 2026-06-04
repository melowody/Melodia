package dev.meluhdy.melodia.utils

import com.jeff_media.customblockdata.CustomBlockData
import com.jeff_media.morepersistentdatatypes.DataType
import org.bukkit.*
import org.bukkit.attribute.AttributeModifier
import org.bukkit.block.Block
import org.bukkit.block.banner.Pattern
import org.bukkit.block.data.BlockData
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.util.BlockVector
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector
import java.util.*

object BlockDataUtils {

    val types = mapOf<Class<*>, PersistentDataType<*, *>>(
        AttributeModifier::class.java to DataType.ATTRIBUTE_MODIFIER,
        Array<AttributeModifier>::class.java to DataType.ATTRIBUTE_MODIFIER_ARRAY,
        BlockData::class.java to DataType.BLOCK_DATA,
        Array<BlockData>::class.java to DataType.BLOCK_DATA_ARRAY,
        BlockVector::class.java to DataType.BLOCK_VECTOR,
        Array<BlockVector>::class.java to DataType.BLOCK_VECTOR_ARRAY,
        BoundingBox::class.java to DataType.BOUNDING_BOX,
        Array<BoundingBox>::class.java to DataType.BOUNDING_BOX_ARRAY,
        Color::class.java to DataType.COLOR,
        Array<Color>::class.java to DataType.COLOR_ARRAY,
        ConfigurationSerializable::class.java to DataType.CONFIGURATION_SERIALIZABLE,
        Array<ConfigurationSerializable>::class.java to DataType.CONFIGURATION_SERIALIZABLE_ARRAY,
        Date::class.java to DataType.DATE,
        FileConfiguration::class.java to DataType.FILE_CONFIGURATION,
        FireworkEffect::class.java to DataType.FIREWORK_EFFECT,
        Array<FireworkEffect>::class.java to DataType.FIREWORK_EFFECT_ARRAY,
        ItemMeta::class.java to DataType.ITEM_META,
        Array<ItemMeta>::class.java to DataType.ITEM_META_ARRAY,
        ItemStack::class.java to DataType.ITEM_STACK,
        Array<ItemStack>::class.java to DataType.ITEM_STACK_ARRAY,
        Location::class.java to DataType.LOCATION,
        Array<Location>::class.java to DataType.LOCATION_ARRAY,
        OfflinePlayer::class.java to DataType.OFFLINE_PLAYER,
        Array<OfflinePlayer>::class.java to DataType.OFFLINE_PLAYER_ARRAY,
        Pattern::class.java to DataType.PATTERN,
        Array<Pattern>::class.java to DataType.PATTERN_ARRAY,
        Player::class.java to DataType.PLAYER,
        Array<Player>::class.java to DataType.PLAYER_ARRAY,
        PotionEffect::class.java to DataType.POTION_EFFECT,
        Array<PotionEffect>::class.java to DataType.POTION_EFFECT_ARRAY,
        UUID::class.java to DataType.UUID,
        Vector::class.java to DataType.VECTOR,
        Array<Vector>::class.java to DataType.VECTOR_ARRAY,
        Boolean::class.java to DataType.BOOLEAN,
        Array<Boolean>::class.java to DataType.BOOLEAN_ARRAY,
        Char::class.java to DataType.CHARACTER,
        Array<Char>::class.java to DataType.CHARACTER_ARRAY,
        Array<Double>::class.java to DataType.DOUBLE_ARRAY,
        Array<Float>::class.java to DataType.FLOAT_ARRAY,
        Array<Short>::class.java to DataType.SHORT_ARRAY,
        Array<String>::class.java to DataType.STRING_ARRAY,
        Byte::class.java to DataType.BYTE,
        Array<Byte>::class.java to DataType.BYTE_ARRAY,
        Double::class.java to DataType.DOUBLE,
        Float::class.java to DataType.FLOAT,
        Int::class.java to DataType.INTEGER,
        Array<Int>::class.java to DataType.INTEGER_ARRAY,
        Long::class.java to DataType.LONG,
        Array<Long>::class.java to DataType.LONG_ARRAY,
        Short::class.java to DataType.SHORT,
        String::class.java to DataType.STRING,
        PersistentDataContainer::class.java to DataType.TAG_CONTAINER,
        Array<PersistentDataContainer>::class.java to DataType.TAG_CONTAINER_ARRAY
    )

    fun Block.removeData(plugin: JavaPlugin, key: NamespacedKey) {
        val data = CustomBlockData(this, plugin)
        data.remove(key)
    }

    inline fun <reified T : Any> Block.getFromTag(plugin: JavaPlugin, key: NamespacedKey): T? = getFromTagImpl(plugin, key, T::class.java)

    @PublishedApi
    internal fun <T : Any> Block.getFromTagImpl(plugin: JavaPlugin, key: NamespacedKey, clazz: Class<T>): T? {
        val type = types[clazz] ?: error("${clazz.simpleName} is not a valid class!")

        @Suppress("UNCHECKED_CAST")
        return CustomBlockData(this, plugin).get(key, type as PersistentDataType<*, T>)
    }

    inline fun <reified T : Any> Block.setTag(plugin: JavaPlugin, key: NamespacedKey, value: T) = setFromTagImpl(plugin, key, value, T::class.java)

    @PublishedApi
    internal fun <T : Any> Block.setFromTagImpl(plugin: JavaPlugin, key: NamespacedKey, value: T, clazz: Class<T>) {
        val type = types[clazz] ?: error("${clazz.simpleName} is not a valid class!")
        @Suppress("UNCHECKED_CAST")
        CustomBlockData(this, plugin).set(key, type as PersistentDataType<*, T>, value)
    }

}