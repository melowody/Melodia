package dev.meluhdy.melodia.misc.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import java.util.UUID

class LocationSerializer: KSerializer<Location> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(Location::class.qualifiedName!!) {
        element("world", UUIDSerializer().descriptor)
        element("x", Double.serializer().descriptor)
        element("y", Double.serializer().descriptor)
        element("z", Double.serializer().descriptor)
        element("yaw", Float.serializer().descriptor)
        element("pitch", Float.serializer().descriptor)
    }

    override fun serialize(encoder: Encoder, value: Location) = encoder.encodeStructure(descriptor) {
        encodeSerializableElement<UUID>(descriptor, 0, UUIDSerializer(), value.world.uid)
        encodeDoubleElement(descriptor, 1, value.x)
        encodeDoubleElement(descriptor, 2, value.y)
        encodeDoubleElement(descriptor, 3, value.z)
        encodeFloatElement(descriptor, 4, value.yaw)
        encodeFloatElement(descriptor, 5, value.pitch)
    }

    override fun deserialize(decoder: Decoder): Location = decoder.decodeStructure(descriptor) {
        var world: World? = null; var x = 0.0; var y = 0.0; var z = 0.0; var yaw = 0.0f; var pitch = 0.0f

        while (true) {
            val index = decodeElementIndex(descriptor)
            when (index) {
                0 -> world = Bukkit.getWorld(UUID.fromString(decodeStringElement(descriptor, index)))
                1 -> x = decodeDoubleElement(descriptor, index)
                2 -> y = decodeDoubleElement(descriptor, index)
                3 -> z = decodeDoubleElement(descriptor, index)
                4 -> yaw = decodeFloatElement(descriptor, index)
                5 -> pitch = decodeFloatElement(descriptor, index)
                CompositeDecoder.DECODE_DONE -> break
            }
        }

        Location(world, x, y, z, yaw, pitch)
    }


}