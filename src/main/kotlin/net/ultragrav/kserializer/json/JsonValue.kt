package net.ultragrav.kserializer.json

data class JsonValue<T>(val value: T, val type: JsonType<T>) {
    fun <I> write(indexable: JsonIndexable<I>, index: I) {
        type.write(indexable, index, value)
    }
    fun add(array: JsonArray, index: Int = -1) {
        type.add(array, value, index)
    }
}