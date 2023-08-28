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

    fun type(key: T): JsonType<*>
    fun <R> get(key: T, type: JsonType<R>): R {
        return type.read(this, key)
    }
    @Suppress("UNCHECKED_CAST")
    fun <R> get(key: T): R {
        return get(key, type(key)) as R
    }
    fun <R> set(key: T, type: JsonType<R>, value: R) {
        type.write(this, key, value)
    }
    fun set(key: T, value: Any?) {
        if (value == null) {
            remove(key)
        } else {
            set(key, JsonType.of(value), value)
        }
    }

    fun remove(key: T): Any?
    fun clear()

    fun contains(key: T): Boolean

    val size: Int
}