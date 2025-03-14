package net.ultragrav.kserializer.json

import java.util.*

sealed interface JsonIndexable<T> {
    fun getString(key: T): String
    fun setString(key: T, value: String): Any?

    fun getObject(key: T): JsonObject
    fun setObject(key: T, data: JsonObject): Any?

    fun getArray(key: T): JsonArray
    fun setArray(key: T, array: JsonArray): Any?

    fun getNumber(key: T): Double
    fun setNumber(key: T, number: Double): Any?

    fun getBoolean(key: T): Boolean
    fun setBoolean(key: T, boolean: Boolean): Any?

    fun getBinary(key: T): BsonBinary
    fun setBinary(key: T, binary: BsonBinary): Any?
    fun setBinary(key: T, type: BsonBinaryType, value: ByteArray): Any? = setBinary(key, BsonBinary(type, value))
    fun setBinary(key: T, value: ByteArray): Any? = setBinary(key, BsonBinaryType.GENERIC, value)

    fun getDate(key: T): Date
    fun setDate(key: T, date: Date): Any?

    fun getInt(key: T): Int
    fun setInt(key: T, int: Int): Any?

    fun getLong(key: T): Long
    fun setLong(key: T, long: Long): Any?

    fun getNull(key: T): Any?
    fun setNull(key: T): Any?

    fun type(key: T): JsonType<*>
    operator fun <R> get(key: T, type: JsonType<R>): R {
        return type.read(this, key)
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <R> get(key: T): R {
        return get(key, type(key)) as R
    }

    operator fun <R> set(key: T, type: JsonType<R>, value: R) {
        type.write(this, key, value)
    }

    operator fun set(key: T, value: Any?) {
        set(key, JsonType.of(value), value)
    }

    fun copy(): JsonIndexable<T>

    fun remove(key: T): Any?
    fun clear()

    operator fun contains(key: T): Boolean

    val size: Int
    val keys: Iterable<T>

    val defaultKey: T


    // Binary types
    fun getUUID(key: T): UUID = get(key, JsonType.UUID)
    fun setUUID(key: T, uuid: UUID): Any? = set(key, JsonType.UUID, uuid)
}