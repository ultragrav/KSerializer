package net.ultragrav.kserializer.kotlinx

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule
import net.ultragrav.kserializer.json.BsonBinaryType
import net.ultragrav.kserializer.json.JsonArray
import net.ultragrav.kserializer.json.JsonIndexable
import net.ultragrav.kserializer.json.JsonObject

@OptIn(ExperimentalSerializationApi::class)
internal class JsonCompositeDecoder<T>(
    override val serializersModule: SerializersModule,
    val json: JsonIndexable<T>
) : CompositeDecoder {
    @Suppress("UNCHECKED_CAST")
    val getKey: (SerialDescriptor, Int) -> T = when (json) {
        is JsonObject -> { descriptor, index -> descriptor.getElementName(index) as T }
        is JsonArray -> { _, index -> index as T }
        else -> throw UnsupportedOperationException()
    }

    override fun decodeSequentially(): Boolean = true

    override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean {
        return json.getBoolean(getKey(descriptor, index))
    }

    override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte {
        return json.getNumber(getKey(descriptor, index)).toByte()
    }

    override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char {
        return json.getString(getKey(descriptor, index))[0]
    }

    override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double {
        return json.getNumber(getKey(descriptor, index)).toDouble()
    }

    // TODO: Better way to do this?
    private var nextElement = 0
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (nextElement >= descriptor.elementsCount) return CompositeDecoder.DECODE_DONE
        return nextElement++
    }

    override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float {
        return json.getNumber(getKey(descriptor, index)).toFloat()
    }

    override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int): Decoder {
        return JsonDecoder(serializersModule, json, getKey(descriptor, index))
    }

    override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int {
        return json.getNumber(getKey(descriptor, index)).toInt()
    }

    override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long {
        return json.getNumber(getKey(descriptor, index)).toLong()
    }

    @ExperimentalSerializationApi
    override fun <T : Any> decodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        previousValue: T?
    ): T? {
        return if (json.contains(getKey(descriptor, index))) {
            val decoder = JsonDecoder(serializersModule, json, getKey(descriptor, index))
            decoder.decodeSerializableValue(deserializer)
        } else {
            null
        }
    }

    override fun decodeShortElement(descriptor: SerialDescriptor, index: Int): Short {
        return json.getNumber(getKey(descriptor, index)).toShort()
    }

    override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String {
        return json.getString(getKey(descriptor, index))
    }

    fun decodeByteArrayElement(descriptor: SerialDescriptor, index: Int): ByteArray {
        val binary = json.getBinary(getKey(descriptor, index))
        if (binary.type != BsonBinaryType.GENERIC)
            throw IllegalArgumentException("Expected generic binary, got ${binary.type}")
        return binary.value
    }

    override fun endStructure(descriptor: SerialDescriptor) {}

    @Suppress("UNCHECKED_CAST")
    override fun <T> decodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        previousValue: T?
    ): T {
        if (deserializer == ByteArraySerializer()) {
            return decodeByteArrayElement(descriptor, index) as T
        }
        val decoder = JsonDecoder(serializersModule, json, getKey(descriptor, index))
        return decoder.decodeSerializableValue(deserializer)
    }

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int {
        return json.size
    }
}