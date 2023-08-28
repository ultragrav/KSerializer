package net.ultragrav.kserializer.kotlinx

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule
import net.ultragrav.kserializer.json.JsonIndexable
import net.ultragrav.kserializer.json.JsonObject

@OptIn(ExperimentalSerializationApi::class)
internal class JsonDecoder<T>(
    override val serializersModule: SerializersModule,
    val json: JsonIndexable<T>,
    val key: T? = null
) : Decoder {
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        return when (descriptor.kind) {
            StructureKind.LIST -> {
                JsonCompositeDecoder(serializersModule, json.getArray(key!!))
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
        return json.getBoolean(key!!)
    }

    override fun decodeByte(): Byte {
        return json.getNumber(key!!).toByte()
    }

    override fun decodeChar(): Char {
        return json.getString(key!!)[0]
    }

    override fun decodeDouble(): Double {
        return json.getNumber(key!!).toDouble()
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        return enumDescriptor.getElementIndex(json.getString(key!!))
    }

    override fun decodeFloat(): Float {
        return json.getNumber(key!!).toFloat()
    }

    override fun decodeInline(descriptor: SerialDescriptor): Decoder {
        return this
    }

    override fun decodeInt(): Int {
        return json.getNumber(key!!).toInt()
    }

    override fun decodeLong(): Long {
        return json.getNumber(key!!).toLong()
    }

    @ExperimentalSerializationApi
    override fun decodeNotNullMark(): Boolean {
        return true
    }

    @ExperimentalSerializationApi
    override fun decodeNull(): Nothing? {
        return null
    }

    override fun decodeShort(): Short {
        return json.getNumber(key!!).toShort()
    }

    override fun decodeString(): String {
        return json.getString(key!!)
    }

    private fun decodeByteArray(): ByteArray {
        return json.getByteArray(key!!)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T {
        if (deserializer == ByteArraySerializer()) {
            return decodeByteArray() as T
        }
        return super.decodeSerializableValue(deserializer)
    }
}