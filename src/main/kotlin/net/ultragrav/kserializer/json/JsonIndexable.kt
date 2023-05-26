package net.ultragrav.kserializer.json

interface JsonIndexable<T> {
    fun getString(key: T): String
    fun setString(key: T, value: String): Any?

    fun getObject(key: T): JsonObject
    fun setObject(key: T, data: JsonObject): Any?

    fun getArray(key: T): JsonArray
    fun setArray(key: T, array: JsonArray): Any?
}