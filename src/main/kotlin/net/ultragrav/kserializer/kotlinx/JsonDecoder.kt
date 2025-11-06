package net.ultragrav.kserializer.kotlinx

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.modules.SerializersModule
import net.ultragrav.kserializer.json.BsonBinaryType
import net.ultragrav.kserializer.json.JsonIndexable
import net.ultragrav.kserializer.json.JsonObject
import net.ultragrav.kserializer.json.JsonType
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
                val key = getKeyOrDefault()
                if (key !in json) error("$key is not a key in ${json.keys}")
                JsonCompositeDecoder(serializersModule, json.getArray(getKeyOrDefault()))
            }

            else -> {
                val obj = if (key == null) json as JsonObject
                else json.getObject(key)
                val sequential = descriptor.elementNames.all {
                    it in obj
                }
                JsonCompositeDecoder(serializersModule, obj, sequential)
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

        val name = json.getString(getKeyOrDefault())

        val res = enumDescriptor.getElementIndex(name)
        if (res != CompositeDecoder.UNKNOWN_NAME) {
            return res
        }

        val idxToNames = (0 until enumDescriptor.elementsCount)
            .associateWith { enumDescriptor.getElementAnnotations(it)
                .filterIsInstance<JsonNames>()
                .flatMap { a -> a.names.toList() }
            }

        for ((idx, names) in idxToNames) {
            if (name in names) {
                return idx
            }
        }

        error("Enum value '$name' not found in ${enumDescriptor.serialName}")
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
        // it will only be null if the object is empty or includes only 1 null.
        val keyOrDefault = getKeyOrDefault()
        val normalCondition = keyOrDefault in json && json.type(keyOrDefault) != JsonType.NULL
        if (normalCondition) return true
        if (key != null) return false
        val defaultKey = json.defaultKey
        if (defaultKey in json) return json.type(defaultKey) != JsonType.NULL
        return json.size > 0
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