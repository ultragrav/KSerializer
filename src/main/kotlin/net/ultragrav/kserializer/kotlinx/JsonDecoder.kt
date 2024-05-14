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
        val key = getKeyOrDefault()
        if (key !in json) error("$key is not a key in ${json.keys}")
        return json.getBoolean(key)
    }

    override fun decodeByte(): Byte {
        val key = getKeyOrDefault()
        if (key !in json) error("$key is not a key in ${json.keys}")
        return json.getInt(key).toByte()
    }

    override fun decodeChar(): Char {
        val key = getKeyOrDefault()
        if (key !in json) error("$key is not a key in ${json.keys}")
        return json.getString(getKeyOrDefault())[0]
    }

    override fun decodeDouble(): Double {
        val key = getKeyOrDefault()
        if (key !in json) error("$key is not a key in ${json.keys}")
        return json.getNumber(getKeyOrDefault())
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        val key = getKeyOrDefault()
        if (key !in json) error("$key is not a key in ${json.keys}")
        return enumDescriptor.getElementIndex(json.getString(getKeyOrDefault()))
    }

    override fun decodeFloat(): Float {
        val key = getKeyOrDefault()
        if (key !in json) error("$key is not a key in ${json.keys}")
        return json.getNumber(getKeyOrDefault()).toFloat()
    }

    override fun decodeInline(descriptor: SerialDescriptor): Decoder {
        return this
    }

    override fun decodeInt(): Int {
        val key = getKeyOrDefault()
        if (key !in json) error("$key is not a key in ${json.keys}")
        return json.getInt(getKeyOrDefault())
    }

    override fun decodeLong(): Long {
        val key = getKeyOrDefault()
        if (key !in json) error("$key is not a key in ${json.keys}")
        return json.getLong(getKeyOrDefault())
    }

    @ExperimentalSerializationApi
    override fun decodeNotNullMark(): Boolean {
        // The 2nd part of this is because structure encoding will just encode into the
        // current object (if key == null), and so when decoding a structure after this check,
        // it will only be null if the object is empty.
        return json.contains(getKeyOrDefault()) || (key == null && json.size > 0)
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
//        if (deserializer == serializer<JsonObject>()) {
//            return json.getObject(getKeyOrDefault()).copy() as T
//        }
//        if (deserializer == serializer<JsonArray>()) {
//            return json.getArray(getKeyOrDefault()).copy() as T
//        }
        return super.decodeSerializableValue(deserializer)
    }
}