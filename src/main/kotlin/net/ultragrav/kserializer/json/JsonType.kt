package net.ultragrav.kserializer.json

import net.ultragrav.kserializer.serialization.JsonDataSerializer
import net.ultragrav.kserializer.util.toBytes
import net.ultragrav.kserializer.util.toUUID
import java.util.Date
import java.util.UUID

interface JsonType<T> : JsonDataSerializer<T> {
    companion object {
        val STRING = object : JsonType<String> {
            override fun <I> write(indexable: JsonIndexable<I>, index: I, value: String): Any? {
                return indexable.setString(index, value)
            }

            override fun <I> read(indexable: JsonIndexable<I>, index: I): String {
                return indexable.getString(index)
            }

            override fun add(array: JsonArray, value: String, index: Int) {
                array.addString(value, index)
            }
        }

        val OBJECT = object : JsonType<JsonObject> {
            override fun <I> write(indexable: JsonIndexable<I>, index: I, value: JsonObject): Any? {
                return indexable.setObject(index, value)
            }

            override fun <I> read(indexable: JsonIndexable<I>, index: I): JsonObject {
                return indexable.getObject(index)
            }

            override fun add(array: JsonArray, value: JsonObject, index: Int) {
                array.addObject(value, index)
            }
        }

        val ARRAY = object : JsonType<JsonArray> {
            override fun <I> write(indexable: JsonIndexable<I>, index: I, value: JsonArray): Any? {
                return indexable.setArray(index, value)
            }

            override fun <I> read(indexable: JsonIndexable<I>, index: I): JsonArray {
                return indexable.getArray(index)
            }

            override fun add(array: JsonArray, value: JsonArray, index: Int) {
                array.addArray(value, index)
            }
        }

        val NUMBER = object : JsonType<Number> {
            override fun <I> write(indexable: JsonIndexable<I>, index: I, value: Number): Any? {
                return indexable.setNumber(index, value)
            }

            override fun <I> read(indexable: JsonIndexable<I>, index: I): Number {
                return indexable.getNumber(index)
            }

            override fun add(array: JsonArray, value: Number, index: Int) {
                array.addNumber(value, index)
            }
        }

        val BOOLEAN = object : JsonType<Boolean> {
            override fun <I> write(indexable: JsonIndexable<I>, index: I, value: Boolean): Any? {
                return indexable.setBoolean(index, value)
            }

            override fun <I> read(indexable: JsonIndexable<I>, index: I): Boolean {
                return indexable.getBoolean(index)
            }

            override fun add(array: JsonArray, value: Boolean, index: Int) {
                array.addBoolean(value, index)
            }
        }

        val BINARY = object : JsonType<BsonBinary> {
            override fun <I> write(indexable: JsonIndexable<I>, index: I, value: BsonBinary): Any? {
                return indexable.setBinary(index, value)
            }

            override fun <I> read(indexable: JsonIndexable<I>, index: I): BsonBinary {
                return indexable.getBinary(index)
            }

            override fun add(array: JsonArray, value: BsonBinary, index: Int) {
                array.addBinary(value, index)
            }
        }

        val GENERIC_BINARY = object : JsonType<ByteArray> {
            override fun <I> write(indexable: JsonIndexable<I>, index: I, value: ByteArray): Any? {
                return indexable.setBinary(index, value)
            }

            override fun <I> read(indexable: JsonIndexable<I>, index: I): ByteArray {
                val binary = indexable.getBinary(index)
                if (binary.type != BsonBinaryType.GENERIC) {
                    throw IllegalArgumentException("Expected generic binary, got ${binary.type}")
                }
                return binary.value
            }

            override fun add(array: JsonArray, value: ByteArray, index: Int) {
                array.addBinary(BsonBinary(BsonBinaryType.GENERIC, value), index)
            }
        }

        val DATE = object : JsonType<Date> {
            override fun <I> write(indexable: JsonIndexable<I>, index: I, value: Date): Any? {
                return indexable.setDate(index, value)
            }

            override fun <I> read(indexable: JsonIndexable<I>, index: I): Date {
                return indexable.getDate(index)
            }

            override fun add(array: JsonArray, value: Date, index: Int) {
                array.addDate(value, index)
            }
        }

        val UUID = object : JsonType<UUID> {
            override fun <I> write(indexable: JsonIndexable<I>, index: I, value: UUID): Any? {
                return indexable.setBinary(index, BsonBinary(BsonBinaryType.UUID, value.toBytes()))
            }

            override fun <I> read(indexable: JsonIndexable<I>, index: I): UUID {
                val binary = indexable.getBinary(index)
                if (binary.type != BsonBinaryType.UUID) {
                    throw IllegalArgumentException("Expected UUID, got ${binary.type}")
                }
                return binary.value.toUUID()
            }

            override fun add(array: JsonArray, value: UUID, index: Int) {
                array.addBinary(BsonBinary(BsonBinaryType.UUID, value.toBytes()), index)
            }
        }

        @Suppress("UNCHECKED_CAST")
        fun <T> of(value: T): JsonType<T> {
            return when (value) {
                is String -> STRING
                is JsonObject -> OBJECT
                is JsonArray -> ARRAY
                is Number -> NUMBER
                is Boolean -> BOOLEAN
                is BsonBinary -> BINARY
                is ByteArray -> GENERIC_BINARY
                is Date -> DATE
                else -> throw IllegalArgumentException("Unknown type: $value")
            } as JsonType<T>
        }
    }

    fun <I> write(indexable: JsonIndexable<I>, index: I, value: T): Any?
    fun <I> read(indexable: JsonIndexable<I>, index: I): T
    fun add(array: JsonArray, value: T, index: Int = -1)

    override fun <K> serialize(data: JsonIndexable<K>, key: K, value: T): Any? {
        return write(data, key, value)
    }

    override fun <K> deserialize(data: JsonIndexable<K>, key: K): T {
        return read(data, key)
    }

    override fun serializeAdd(data: JsonArray, value: T, index: Int) {
        add(data, value, index)
    }
}