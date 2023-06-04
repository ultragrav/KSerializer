package net.ultragrav.kserializer

import net.ultragrav.kserializer.json.JsonArray
import net.ultragrav.kserializer.json.JsonIndexable
import net.ultragrav.kserializer.json.JsonObject
import net.ultragrav.kserializer.serialization.JsonDataSerializer
import net.ultragrav.serializer.GravSerializable
import net.ultragrav.serializer.GravSerializer
import java.math.BigDecimal
import java.math.BigInteger

object Serializers {
    val STRING = object : JsonDataSerializer<String> {
        override fun <K> serialize(data: JsonIndexable<K>, key: K, value: String): Any? {
            return data.setString(key, value)
        }

        override fun <K> deserialize(data: JsonIndexable<K>, key: K): String {
            return data.getString(key)
        }

        override fun serializeAdd(data: JsonArray, value: String, index: Int) {
            data.addString(value, index)
        }
    }

    val INT = object : JsonDataSerializer<Int> {
        override fun <K> serialize(data: JsonIndexable<K>, key: K, value: Int): Any? {
            return data.setNumber(key, value)
        }

        override fun <K> deserialize(data: JsonIndexable<K>, key: K): Int {
            return data.getNumber(key).toInt()
        }

        override fun serializeAdd(data: JsonArray, value: Int, index: Int) {
            data.addNumber(value, index)
        }
    }

    val LONG = object : JsonDataSerializer<Long> {
        override fun <K> serialize(data: JsonIndexable<K>, key: K, value: Long): Any? {
            return data.setNumber(key, value)
        }

        override fun <K> deserialize(data: JsonIndexable<K>, key: K): Long {
            return data.getNumber(key).toLong()
        }

        override fun serializeAdd(data: JsonArray, value: Long, index: Int) {
            data.addNumber(value, index)
        }
    }

    val DOUBLE = object : JsonDataSerializer<Double> {
        override fun <K> serialize(data: JsonIndexable<K>, key: K, value: Double): Any? {
            return data.setNumber(key, value)
        }

        override fun <K> deserialize(data: JsonIndexable<K>, key: K): Double {
            return data.getNumber(key).toDouble()
        }

        override fun serializeAdd(data: JsonArray, value: Double, index: Int) {
            data.addNumber(value, index)
        }
    }

    val FLOAT = object : JsonDataSerializer<Float> {
        override fun <K> serialize(data: JsonIndexable<K>, key: K, value: Float): Any? {
            return data.setNumber(key, value)
        }

        override fun <K> deserialize(data: JsonIndexable<K>, key: K): Float {
            return data.getNumber(key).toFloat()
        }

        override fun serializeAdd(data: JsonArray, value: Float, index: Int) {
            data.addNumber(value, index)
        }
    }

    val BOOLEAN = object : JsonDataSerializer<Boolean> {
        override fun <K> serialize(data: JsonIndexable<K>, key: K, value: Boolean): Any? {
            return data.setBoolean(key, value)
        }

        override fun <K> deserialize(data: JsonIndexable<K>, key: K): Boolean {
            return data.getBoolean(key)
        }

        override fun serializeAdd(data: JsonArray, value: Boolean, index: Int) {
            data.addBoolean(value, index)
        }
    }

    val JSON_OBJECT = object : JsonDataSerializer<JsonObject> {
        override fun <K> serialize(data: JsonIndexable<K>, key: K, value: JsonObject): Any? {
            return data.setObject(key, value)
        }

        override fun <K> deserialize(data: JsonIndexable<K>, key: K): JsonObject {
            return data.getObject(key)
        }

        override fun serializeAdd(data: JsonArray, value: JsonObject, index: Int) {
            data.addObject(value, index)
        }
    }

    val JSON_ARRAY = object : JsonDataSerializer<JsonArray> {
        override fun <K> serialize(data: JsonIndexable<K>, key: K, value: JsonArray): Any? {
            return data.setArray(key, value)
        }

        override fun <K> deserialize(data: JsonIndexable<K>, key: K): JsonArray {
            return data.getArray(key)
        }

        override fun serializeAdd(data: JsonArray, value: JsonArray, index: Int) {
            data.addArray(value, index)
        }
    }

    val BYTE_ARRAY = object : JsonDataSerializer<ByteArray> {
        override fun <K> serialize(data: JsonIndexable<K>, key: K, value: ByteArray): Any? {
            return data.setByteArray(key, value)
        }

        override fun <K> deserialize(data: JsonIndexable<K>, key: K): ByteArray {
            return data.getByteArray(key)
        }

        override fun serializeAdd(data: JsonArray, value: ByteArray, index: Int) {
            data.addByteArray(value, index)
        }
    }

    val BIG_INTEGER = object : JsonDataSerializer<BigInteger> {
        override fun <K> serialize(data: JsonIndexable<K>, key: K, value: BigInteger): Any? {
            return data.setByteArray(key, value.toByteArray())
        }

        override fun <K> deserialize(data: JsonIndexable<K>, key: K): BigInteger {
            return BigInteger(data.getByteArray(key))
        }

        override fun serializeAdd(data: JsonArray, value: BigInteger, index: Int) {
            data.addByteArray(value.toByteArray(), index)
        }
    }

    val BIG_DECIMAL = object : JsonDataSerializer<BigDecimal> {
        override fun <K> serialize(data: JsonIndexable<K>, key: K, value: BigDecimal): Any? {
            val ser = GravSerializer()
            ser.writeInt(value.scale())
            ser.append(value.unscaledValue().toByteArray())
            return data.setByteArray(key, ser.toByteArray())
        }

        override fun <K> deserialize(data: JsonIndexable<K>, key: K): BigDecimal {
            val bytes = data.getByteArray(key)
            val ser = GravSerializer(bytes)
            val scale = ser.readInt()
            val unscaled = ser.readBytes(ser.remaining)
            return BigDecimal(BigInteger(unscaled), scale)
        }

        override fun serializeAdd(data: JsonArray, value: BigDecimal, index: Int) {
            val ser = GravSerializer()
            ser.writeInt(value.scale())
            ser.append(value.unscaledValue().toByteArray())
            data.addByteArray(ser.toByteArray(), index)
        }
    }

    fun <T : Enum<T>> enum(clazz: Class<T>): JsonDataSerializer<T> {
        return object : JsonDataSerializer<T> {
            override fun <K> serialize(data: JsonIndexable<K>, key: K, value: T): Any? {
                return data.setString(key, value.name)
            }

            override fun <K> deserialize(data: JsonIndexable<K>, key: K): T {
                return java.lang.Enum.valueOf(clazz, data.getString(key))
            }

            override fun serializeAdd(data: JsonArray, value: T, index: Int) {
                data.addString(value.name, index)
            }
        }
    }

    fun <T : GravSerializable> serializable(clazz: Class<T>): JsonDataSerializer<T> {
        return object : JsonDataSerializer<T> {
            override fun <K> serialize(data: JsonIndexable<K>, key: K, value: T): Any? {
                val ser = GravSerializer()
                value.serialize(ser)
                return data.setByteArray(key, ser.toByteArray())
            }

            override fun <K> deserialize(data: JsonIndexable<K>, key: K): T {
                return GravSerializable.deserialize(clazz, GravSerializer(data.getByteArray((key))))
            }

            override fun serializeAdd(data: JsonArray, value: T, index: Int) {
                val ser = GravSerializer()
                value.serialize(ser)
                data.addByteArray(ser.toByteArray(), index)
            }
        }
    }
}