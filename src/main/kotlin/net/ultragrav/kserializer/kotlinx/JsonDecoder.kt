package net.ultragrav.kserializer.kotlinx

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import net.ultragrav.kserializer.json.BsonBinaryType
import net.ultragrav.kserializer.json.JsonArray
import net.ultragrav.kserializer.json.JsonIndexable
import net.ultragrav.kserializer.json.JsonObject
import net.ultragrav.kserializer.util.toUUID
import java.util.*

@OptIn(ExperimentalSerializationApi::class)
internal class JsonDecoder<T>(
    override val serializersModule: SerializersModule,
    val json: JsonIndexable<T>,
    val key: T? = null
) : Decoder {
    private fun getKeyOrDefault(): T {
        return key ?: json.defaultKey
    }
    
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        return when (descriptor.kind) {
            StructureKind.LIST, StructureKind.MAP -> {
                JsonCompositeDecoder(serializersModule, json.getArray(getKeyOrDefault()))
            }

            else -> {
                if (key == null) {
                    JsonCompositeDecoder(serializersModule, json as JsonObject)
                } else {
                    JsonCompositeDecoder(serializersModule, json.getObject(key))
                }
            }
        }
    }

    override fun decodeBoolean(): Boolean {
        return json.getBoolean(getKeyOrDefault())
    }

    override fun decodeByte(): Byte {
        return json.getInt(getKeyOrDefault()).toByte()
    }

    override fun decodeChar(): Char {
        return json.getString(getKeyOrDefault())[0]
    }

    override fun decodeDouble(): Double {
        return json.getNumber(getKeyOrDefault())
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        return enumDescriptor.getElementIndex(json.getString(getKeyOrDefault()))
    }

    override fun decodeFloat(): Float {
        return json.getNumber(getKeyOrDefault()).toFloat()
    }

    override fun decodeInline(descriptor: SerialDescriptor): Decoder {
        return this
    }

    override fun decodeInt(): Int {
        return json.getInt(getKeyOrDefault())
    }

    override fun decodeLong(): Long {
        return json.getLong(getKeyOrDefault())
    }

    @ExperimentalSerializationApi
    override fun decodeNotNullMark(): Boolean {
        return json.contains(getKeyOrDefault())
    }

    @ExperimentalSerializationApi
    override fun decodeNull(): Nothing? {
        return null
    }

    override fun decodeShort(): Short {
        return json.getInt(getKeyOrDefault()).toShort()
    }

    override fun decodeString(): String {
        return json.getString(getKeyOrDefault())
    }

    private fun decodeByteArray(): ByteArray {
        return json.getBinary(getKeyOrDefault()).value
    }

    fun decodeDate(): Date {
        return json.getDate(getKeyOrDefault())
    }

    fun decodeUUID(): UUID {
        val binary = json.getBinary(getKeyOrDefault())
        if (binary.type != BsonBinaryType.UUID)
            throw IllegalArgumentException("Expected UUID, got ${binary.type}")
        return binary.value.toUUID()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T {
        if (deserializer == ByteArraySerializer()) {
            return decodeByteArray() as T
        }
        if (deserializer == serializer<JsonObject>()) {
            return json.getObject(getKeyOrDefault()).copy() as T
        }
        if (deserializer == serializer<JsonArray>()) {
            return json.getArray(getKeyOrDefault()).copy() as T
        }
        return super.decodeSerializableValue(deserializer)
    }
}