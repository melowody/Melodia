package dev.meluhdy.melodia.utils.uuid

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import java.util.*

/**
 * The serializer used to save UUIDNameConverter objects.
 */
object UUIDNameConverterSerializer : KSerializer<UUIDNameConverter> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("UUIDNameConverter") {
        element<String>("uuid")
        element<String>("name")
        element<Long>("timeStamp")
    }

    override fun deserialize(decoder: Decoder): UUIDNameConverter {
        return decoder.decodeStructure(descriptor) {
            var uuid = UUID.randomUUID()
            var name = ""
            var time = 0L
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> uuid = UUID.fromString(decodeStringElement(descriptor, 0))
                    1 -> name = decodeStringElement(descriptor, 1)
                    2 -> time = decodeLongElement(descriptor, 2)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> throw IndexOutOfBoundsException("Unexpected Index: $index")
                }
            }
            UUIDNameConverter(uuid, name, time)
        }
    }

    override fun serialize(encoder: Encoder, value: UUIDNameConverter) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.uuid.toString())
            encodeStringElement(descriptor, 1, value.name)
            encodeLongElement(descriptor, 2, value.timestamp)
        }
    }
}