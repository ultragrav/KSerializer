package net.ultragrav.kserializer.kotlinx

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import net.ultragrav.kserializer.json.BsonBinaryType
import net.ultragrav.kserializer.json.JsonArray
import net.ultragrav.kserializer.json.JsonIndexable
import net.ultragrav.kserializer.json.JsonObject
import net.ultragrav.kserializer.util.toBytes
import java.util.*

@OptIn(ExperimentalSerializationApi::class)
internal class JsonEncoder<T>(
    override val serializersModule: SerializersModule,
    val json: JsonIndexable<T>,
    val key: T? = null
) : Encoder {
    private var first = true
    private fun getKeyOrDefault(): T {
        if (key == null && !first) throw IllegalStateException("Cannot encode multiple values to default key")
        first = false
        return key ?: json.defaultKey
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        return when (descriptor.kind) {
            StructureKind.LIST, StructureKind.MAP -> {
                val arr = JsonArray()
                val encoder = JsonCompositeEncoder(serializersModule, arr)
                json.setArray(getKeyOrDefault(), arr)
                return encoder
            }

            else -> {
                if (key == null) {
                    JsonCompositeEncoder(serializersModule, json)
                } else {
                    val encoder = JsonCompositeEncoder(serializersModule, JsonObject())
                    json.setObject(key, encoder.json as JsonObject)
                    encoder
                }
            }
        }
    }

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        val arr = JsonArray(descriptor.elementsCount * collectionSize)
        val encoder = JsonCompositeEncoder(serializersModule, arr)
        json.setArray(getKeyOrDefault(), arr)
        return encoder
    }

    override fun encodeBoolean(value: Boolean) {
        json.setBoolean(getKeyOrDefault(), value)
    }

    override fun encodeByte(value: Byte) {
        json.setInt(getKeyOrDefault(), value.toInt()) // TODO: Better encoding?
    }

    override fun encodeChar(value: Char) {
        json.setString(getKeyOrDefault(), value.toString())
    }

    override fun encodeDouble(value: Double) {
        json.setNumber(getKeyOrDefault(), value)
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        json.setString(getKeyOrDefault(), enumDescriptor.getElementName(index))
    }

    override fun encodeFloat(value: Float) {
        json.setNumber(getKeyOrDefault(), value.toDouble())
    }

    override fun encodeInline(descriptor: SerialDescriptor): Encoder {
        return this
    }

    override fun encodeInt(value: Int) {
        json.setInt(getKeyOrDefault(), value)
    }

    override fun encodeLong(value: Long) {
        json.setLong(getKeyOrDefault(), value)
    }

    @ExperimentalSerializationApi
    override fun encodeNull() {
    }

    override fun encodeShort(value: Short) {
        json.setInt(getKeyOrDefault(), value.toInt())
    }

    override fun encodeString(value: String) {
        json.setString(getKeyOrDefault(), value)
    }

    private fun encodeByteArray(value: ByteArray) {
        json.setBinary(getKeyOrDefault(), value)
    }

    fun encodeDate(value: Date) {
        json.setDate(getKeyOrDefault(), value)
    }

    fun encodeUUID(value: UUID) {
        json.setBinary(getKeyOrDefault(), BsonBinaryType.UUID, value.toBytes())
    }

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        if (serializer == ByteArraySerializer()) {
            encodeByteArray(value as ByteArray)
            return
        }
        if (serializer == serializer<JsonObject>()) {
            json.setObject(getKeyOrDefault(), (value as JsonObject).copy())
            return
        }
        if (serializer == serializer<JsonArray>()) {
            json.setArray(getKeyOrDefault(), (value as JsonArray).copy())
            return
        }
        super.encodeSerializableValue(serializer, value)
    }
}