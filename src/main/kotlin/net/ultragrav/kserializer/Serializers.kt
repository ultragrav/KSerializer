package net.ultragrav.kserializer

import net.ultragrav.kserializer.json.JsonArray
import net.ultragrav.kserializer.json.JsonIndexable
import net.ultragrav.kserializer.json.JsonObject
import net.ultragrav.kserializer.serialization.JsonDataSerializer
import kotlin.reflect.KClass

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

    fun <T : Enum<T>> enum(clazz: KClass<T>): JsonDataSerializer<T> {
        return object : JsonDataSerializer<T> {
            override fun <K> serialize(data: JsonIndexable<K>, key: K, value: T): Any? {
                return data.setString(key, value.name)
            }

            override fun <K> deserialize(data: JsonIndexable<K>, key: K): T {
                return java.lang.Enum.valueOf(clazz.java, data.getString(key))
            }

            override fun serializeAdd(data: JsonArray, value: T, index: Int) {
                data.addString(value.name, index)
            }
        }
    }
}