package net.ultragrav.kserializer.kotlinx

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import net.ultragrav.kserializer.json.JsonArray
import net.ultragrav.kserializer.json.JsonIndexable
import net.ultragrav.kserializer.json.JsonObject

@OptIn(ExperimentalSerializationApi::class)
internal class JsonEncoder<T>(
    override val serializersModule: SerializersModule,
    val json: JsonIndexable<T>,
    val key: T? = null
) : Encoder {
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        return when (descriptor.kind) {
            StructureKind.LIST -> {
                val encoder = JsonCompositeEncoder(serializersModule, JsonArray())
                json.setArray(key!!, encoder.json as JsonArray)
                encoder
            }
            else -> {
                if (key == null) {
                    JsonCompositeEncoder(serializersModule, json as JsonObject)
                } else {
                    val encoder = JsonCompositeEncoder(serializersModule, JsonObject())
                    json.setObject(key, encoder.json as JsonObject)
                    encoder
                }
            }
        }
    }

    override fun encodeBoolean(value: Boolean) {
        json.setBoolean(key!!, value)
    }

    override fun encodeByte(value: Byte) {
        json.setNumber(key!!, value)
    }

    override fun encodeChar(value: Char) {
        json.setString(key!!, value.toString())
    }

    override fun encodeDouble(value: Double) {
        json.setNumber(key!!, value)
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        json.setString(key!!, enumDescriptor.getElementName(index))
    }

    override fun encodeFloat(value: Float) {
        json.setNumber(key!!, value)
    }

    override fun encodeInline(descriptor: SerialDescriptor): Encoder {
        return this
    }

    override fun encodeInt(value: Int) {
        json.setNumber(key!!, value)
    }

    override fun encodeLong(value: Long) {
        json.setNumber(key!!, value)
    }

    @ExperimentalSerializationApi
    override fun encodeNull() {
    }

    override fun encodeShort(value: Short) {
        json.setNumber(key!!, value)
    }

    override fun encodeString(value: String) {
        json.setString(key!!, value)
    }

    private fun encodeByteArray(value: ByteArray) {
        json.setByteArray(key!!, value)
    }

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        if (serializer == ByteArraySerializer()) {
            encodeByteArray(value as ByteArray)
            return
        }
        super.encodeSerializableValue(serializer, value)
    }
}