package net.ultragrav.kserializer

import net.ultragrav.kserializer.json.*
import net.ultragrav.kserializer.serialization.JsonDataSerializer
import net.ultragrav.serializer.GravSerializable
import net.ultragrav.serializer.GravSerializer
import java.math.BigDecimal
import java.math.BigInteger

object Serializers {
    val STRING = JsonType.STRING
    val JSON_OBJECT = JsonType.OBJECT
    val JSON_ARRAY = JsonType.ARRAY
    val BOOLEAN = JsonType.BOOLEAN

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

    val BIG_INTEGER = object : JsonDataSerializer<BigInteger> {
        override fun <K> serialize(data: JsonIndexable<K>, key: K, value: BigInteger): Any? {
            return data.setBinary(key, BsonBinaryType.USER_DEFINED, value.toByteArray())
        }

        override fun <K> deserialize(data: JsonIndexable<K>, key: K): BigInteger {
            val binary = data.getBinary(key)
            if (binary.type != BsonBinaryType.USER_DEFINED)
                throw IllegalArgumentException("Expected user-defined binary, got ${binary.type}")
            return BigInteger(binary.value)
        }

        override fun serializeAdd(data: JsonArray, value: BigInteger, index: Int) {
            data.addBinary(BsonBinaryType.USER_DEFINED, value.toByteArray(), index)
        }
    }

    val BIG_DECIMAL = object : JsonDataSerializer<BigDecimal> {
        override fun <K> serialize(data: JsonIndexable<K>, key: K, value: BigDecimal): Any? {
            val ser = GravSerializer()
            ser.writeInt(value.scale())
            ser.append(value.unscaledValue().toByteArray())
            return data.setBinary(key, BsonBinaryType.USER_DEFINED, ser.toByteArray())
        }

        override fun <K> deserialize(data: JsonIndexable<K>, key: K): BigDecimal {
            val binary = data.getBinary(key)
            if (binary.type != BsonBinaryType.USER_DEFINED)
                throw IllegalArgumentException("Expected user-defined binary, got ${binary.type}")
            val bytes = binary.value
            val ser = GravSerializer(bytes)
            val scale = ser.readInt()
            val unscaled = ser.readBytes(ser.remaining)
            return BigDecimal(BigInteger(unscaled), scale)
        }

        override fun serializeAdd(data: JsonArray, value: BigDecimal, index: Int) {
            val ser = GravSerializer()
            ser.writeInt(value.scale())
            ser.append(value.unscaledValue().toByteArray())
            data.addBinary(BsonBinaryType.USER_DEFINED, ser.toByteArray(), index)
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
                return data.setBinary(key, BsonBinaryType.USER_DEFINED, ser.toByteArray())
            }

            override fun <K> deserialize(data: JsonIndexable<K>, key: K): T {
                val binary = data.getBinary(key)
                if (binary.type != BsonBinaryType.USER_DEFINED)
                    throw IllegalArgumentException("Expected user-defined binary, got ${binary.type}")
                return GravSerializable.deserialize(clazz, GravSerializer(binary.value))
            }

            override fun serializeAdd(data: JsonArray, value: T, index: Int) {
                val ser = GravSerializer()
                value.serialize(ser)
                data.addBinary(BsonBinaryType.USER_DEFINED, ser.toByteArray(), index)
            }
        }
    }
}