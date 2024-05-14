package net.ultragrav.kserializer.kotlinx

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
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
    }

    override fun decodeSequentially(): Boolean = true

    override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean {
        val key = getKey(descriptor, index)
        if (key !in json) error("$key is not a key in ${json.keys}")
        return json.getBoolean(key)
    }

    override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte {
        val key = getKey(descriptor, index)
        if (key !in json) error("$key is not a key in ${json.keys}")
        return json.getInt(key).toByte()
    }

    override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char {
        val key = getKey(descriptor, index)
        if (key !in json) error("$key is not a key in ${json.keys}")
        return json.getString(key)[0]
    }

    override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double {
        val key = getKey(descriptor, index)
        if (key !in json) error("$key is not a key in ${json.keys}")
        return json.getNumber(key)
    }

    // TODO: Better way to do this?
    private var nextElement = 0
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (nextElement >= descriptor.elementsCount) return CompositeDecoder.DECODE_DONE
        return nextElement++
    }

    override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float {
        val key = getKey(descriptor, index)
        if (key !in json) error("$key is not a key in ${json.keys}")
        return json.getNumber(key).toFloat()
    }

    override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int): Decoder {
        return JsonDecoder(serializersModule, json, getKey(descriptor, index))
    }

    override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int {
        val key = getKey(descriptor, index)
        if (key !in json) error("$key is not a key in ${json.keys}")
        return json.getInt(key)
    }

    override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long {
        val key = getKey(descriptor, index)
        if (key !in json) error("$key is not a key in ${json.keys}")
        return json.getLong(key)
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
        val key = getKey(descriptor, index)
        if (key !in json) error("$key is not a key in ${json.keys}")
        return json.getInt(key).toShort()
    }

    override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String {
        val key = getKey(descriptor, index)
        if (key !in json) error("$key is not a key in ${json.keys}")
        return json.getString(key)
    }

    fun decodeByteArrayElement(descriptor: SerialDescriptor, index: Int): ByteArray {
        val key = getKey(descriptor, index)
        if (key !in json) error("$key is not a key in ${json.keys}")
        val binary = json.getBinary(key)
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
//        if (deserializer == serializer<JsonObject>()) {
//            return json.getObject(getKey(descriptor, index)).copy() as T
//        }
//        if (deserializer == serializer<JsonArray>()) {
//            return json.getArray(getKey(descriptor, index)).copy() as T
//        }
        val decoder = JsonDecoder(serializersModule, json, getKey(descriptor, index))
        return decoder.decodeSerializableValue(deserializer)
    }

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int {
        return json.size / descriptor.elementsCount
    }
}