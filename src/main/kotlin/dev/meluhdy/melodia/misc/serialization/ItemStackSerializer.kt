package dev.meluhdy.melodia.misc.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.inventory.ItemStack

class ItemStackSerializer: KSerializer<ItemStack> {

    override val descriptor: SerialDescriptor = ByteArraySerializer().descriptor

    override fun serialize(encoder: Encoder, value: ItemStack) = encoder.encodeSerializableValue(ByteArraySerializer(), value.serializeAsBytes())

    override fun deserialize(decoder: Decoder): ItemStack = ItemStack.deserializeBytes(decoder.decodeSerializableValue(ByteArraySerializer()))

}