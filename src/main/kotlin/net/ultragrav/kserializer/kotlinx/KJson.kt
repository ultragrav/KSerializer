package net.ultragrav.kserializer.kotlinx

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.serializer
import net.ultragrav.kserializer.json.JsonObject
import net.ultragrav.kserializer.kotlinx.KJson.Companion.defaultModule
import net.ultragrav.serializer.GravSerializer
import java.util.*

class KJson(val module: SerializersModule = defaultModule) {
    companion object {
        val defaultModule = SerializersModule {
            contextual(Date::class, DateSerializer)
            contextual(UUID::class, UUIDSerializer)
        }
        val DEFAULT = KJson(defaultModule)

        fun encodeToByteArray(obj: Any): ByteArray = DEFAULT.encodeToByteArray(obj)
        inline fun <reified T> decodeFromByteArray(bytes: ByteArray): T = DEFAULT.decodeFromByteArray(bytes)
        fun encodeToByteArray(module: SerializersModule, obj: Any): ByteArray = KJson(module).encodeToByteArray(obj)
        inline fun <reified T> decodeFromByteArray(module: SerializersModule, bytes: ByteArray): T = KJson(module).decodeFromByteArray(bytes)
    }

    fun <T> encode(serializer: SerializationStrategy<T>, obj: T): JsonObject {
        val encoder = JsonEncoder(module, JsonObject())
        encoder.encodeSerializableValue(serializer, obj)
        return encoder.json as JsonObject
    }

    inline fun <reified T> encode(obj: T): JsonObject = encode(module.serializer(), obj)

    fun <T> decode(deserializer: DeserializationStrategy<T>, json: JsonObject): T {
        val decoder = JsonDecoder(module, json)
        return decoder.decodeSerializableValue(deserializer)
    }

    inline fun <reified T> decode(json: JsonObject): T = decode(module.serializer(), json)

    fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, obj: T): ByteArray {
        val encoder = JsonEncoder(module, JsonObject())
        encoder.encodeSerializableValue(serializer, obj)
        val ser = GravSerializer()
        (encoder.json as JsonObject).serialize(ser)

        return ser.toByteArray()
    }

    inline fun <reified T> encodeToByteArray(obj: T): ByteArray = encodeToByteArray(module.serializer(), obj)

    fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T {
        val ser = GravSerializer(bytes)
        val json = JsonObject.deserialize(ser)
        val decoder = JsonDecoder(module, json)
        return decoder.decodeSerializableValue(deserializer)
    }

    inline fun <reified T> decodeFromByteArray(bytes: ByteArray): T =
        decodeFromByteArray(module.serializer(), bytes)
}