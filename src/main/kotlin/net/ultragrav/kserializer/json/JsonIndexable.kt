package net.ultragrav.kserializer.json

interface JsonIndexable<T> {
    fun getString(key: T): String
    fun setString(key: T, value: String): Any?

    fun getObject(key: T): JsonObject
    fun setObject(key: T, data: JsonObject): Any?

    fun getArray(key: T): JsonArray
    fun setArray(key: T, array: JsonArray): Any?

    fun getNumber(key: T): Number
    fun setNumber(key: T, number: Number): Any?

    fun getBoolean(key: T): Boolean
    fun setBoolean(key: T, boolean: Boolean): Any?

    fun getByteArray(key: T): ByteArray
    fun setByteArray(key: T, byteArray: ByteArray): Any?
}