package net.ultragrav.kserializer.kotlinx

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import net.ultragrav.kserializer.json.JsonArray
import net.ultragrav.kserializer.json.JsonIndexable
import net.ultragrav.kserializer.json.JsonObject
import net.ultragrav.kserializer.json.JsonType

@OptIn(ExperimentalSerializationApi::class)
internal class JsonCompositeEncoder<T>(
    override val serializersModule: SerializersModule,
    val json: JsonIndexable<T>
) : CompositeEncoder {
    @Suppress("UNCHECKED_CAST")
    val getKey: (SerialDescriptor, Int) -> T = when(json) {
        is JsonObject -> { descriptor, index -> descriptor.getElementName(index) as T }
        is JsonArray -> { _, index -> index as T }
    }

    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) {
        json.setBoolean(getKey(descriptor, index), value)
    }

    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) {
        json.setInt(getKey(descriptor, index), value.toInt())
    }

    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) {
        json.setString(getKey(descriptor, index), value.toString())
    }

    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) {
        json.setNumber(getKey(descriptor, index), value)
    }

    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) {
        json.setNumber(getKey(descriptor, index), value.toDouble())
    }

    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder {
        return JsonEncoder(serializersModule, json, getKey(descriptor, index))
    }

    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) {
        json.setInt(getKey(descriptor, index), value)
    }

    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) {
        json.setLong(getKey(descriptor, index), value)
    }

    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) {
        json.setInt(getKey(descriptor, index), value.toInt())
    }

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
        json.setString(getKey(descriptor, index), value)
    }

    private fun encodeByteArrayElement(descriptor: SerialDescriptor, index: Int, value: ByteArray) {
        json.setBinary(getKey(descriptor, index), value)
    }

    override fun endStructure(descriptor: SerialDescriptor) {}

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?
    ) {
        if (value == null) {
            json[getKey(descriptor, index), JsonType.NULL] = null
        } else {
            encodeSerializableElement(descriptor, index, serializer, value)
        }
    }

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T
    ) {
        if (serializer == ByteArraySerializer()) {
            encodeByteArrayElement(descriptor, index, value as ByteArray)
            return
        }
//        if (serializer == serializer<JsonObject>()) {
//            json.setObject(getKey(descriptor, index), (value as JsonObject).copy())
//            return
//        }
//        if (serializer == serializer<JsonArray>()) {
//            json.setArray(getKey(descriptor, index), (value as JsonArray).copy())
//            return
//        }
        val encoder = JsonEncoder(serializersModule, json, getKey(descriptor, index))
        serializer.serialize(encoder, value)
    }
}