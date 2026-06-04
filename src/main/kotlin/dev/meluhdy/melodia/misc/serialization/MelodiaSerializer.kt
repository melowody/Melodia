package dev.meluhdy.melodia.misc.serialization

import dev.meluhdy.melodia.manager.MelodiaItem
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import java.util.*

abstract class MelodiaSerializer<T: MelodiaItem>: KSerializer<T> {

    abstract class Builder<T> {

        lateinit var uuid: UUID

        fun isUUIDInitialized(): Boolean = ::uuid.isInitialized

        abstract fun build(): T

    }

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor(this::class.qualifiedName!!) {
            element("uuid", String.serializer().descriptor)
            steps.forEach {
                element(it.name, it.serializer.descriptor)
            }
        }

    abstract fun getBuilder(): Builder<T>

    abstract val steps: Array<SerializerElement<*, T>>

    override fun deserialize(decoder: Decoder): T = decoder.decodeStructure(descriptor) {
        val builder = getBuilder()
        while (true) {
            val index = decodeElementIndex(descriptor)
            if (index == 0) builder.uuid = UUID.fromString(decodeStringElement(descriptor, index))
            else if (index == CompositeDecoder.DECODE_DONE) break
            else {
                val step = steps[index - 1]
                @Suppress("UNCHECKED_CAST")
                (step.decode as (Any?, Builder<T>) -> Unit)(decodeSerializableElement(descriptor, index, step.serializer as KSerializer<Any?>), builder)
            }
        }
        if (!builder.isUUIDInitialized()) {
            throw SerializationException("Missing UUID field!")
        }
        builder.build()
    }

    override fun serialize(encoder: Encoder, value: T) = encoder.encodeStructure(descriptor) {
        encodeStringElement(descriptor, 0, value.uuid.toString())
        steps.forEachIndexed { index, step ->
            @Suppress("UNCHECKED_CAST")
            encodeSerializableElement(descriptor, index + 1, step.serializer as KSerializer<Any?>, step.encode(value))
        }
    }

}

data class SerializerElement<K, T: MelodiaItem>(val name: String, val serializer: KSerializer<K>, val encode: (T) -> K, val decode: (K, MelodiaSerializer.Builder<T>) -> Unit)