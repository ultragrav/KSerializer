package net.ultragrav.kserializer.kotlinx

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.serializer
import net.ultragrav.kserializer.json.JsonObject
import net.ultragrav.serializer.GravSerializer

object KJson {
    val serializersModule = EmptySerializersModule()

    fun <T> encode(serializer: SerializationStrategy<T>, obj: T): JsonObject {
        val encoder = JsonEncoder(serializersModule, JsonObject())
        encoder.encodeSerializableValue(serializer, obj)
        return encoder.json as JsonObject
    }

    inline fun <reified T> encode(obj: T): JsonObject = encode(serializersModule.serializer(), obj)

    fun <T> decode(deserializer: DeserializationStrategy<T>, json: JsonObject): T {
        val decoder = JsonDecoder(serializersModule, json)
        return decoder.decodeSerializableValue(deserializer)
    }

    inline fun <reified T> decode(json: JsonObject): T = decode(serializersModule.serializer(), json)

    fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, obj: T): ByteArray {
        val encoder = JsonEncoder(serializersModule, JsonObject())
        encoder.encodeSerializableValue(serializer, obj)
        val ser = GravSerializer()
        (encoder.json as JsonObject).serialize(ser)
        return ser.toByteArray()
    }

    inline fun <reified T> encodeToByteArray(obj: T): ByteArray = encodeToByteArray(serializersModule.serializer(), obj)

    fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T {
        val ser = GravSerializer(bytes)
        val json = JsonObject.deserialize(ser)
        val decoder = JsonDecoder(serializersModule, json)
        return decoder.decodeSerializableValue(deserializer)
    }

    inline fun <reified T> decodeFromByteArray(bytes: ByteArray): T =
        decodeFromByteArray(serializersModule.serializer(), bytes)
}