package net.ultragrav.kserializer.json

interface JsonType<T> {
    companion object {
        val STRING = object : JsonType<String> {
            override fun <I> write(indexable: JsonIndexable<I>, index: I, value: String) {
                indexable.setString(index, value)
            }

            override fun <I> read(indexable: JsonIndexable<I>, index: I): String {
                return indexable.getString(index)
            }
        }

        val OBJECT = object : JsonType<JsonObject> {
            override fun <I> write(indexable: JsonIndexable<I>, index: I, value: JsonObject) {
                indexable.setObject(index, value)
            }

            override fun <I> read(indexable: JsonIndexable<I>, index: I): JsonObject {
                return indexable.getObject(index)
            }
        }

        val ARRAY = object : JsonType<JsonArray> {
            override fun <I> write(indexable: JsonIndexable<I>, index: I, value: JsonArray) {
                indexable.setArray(index, value)
            }

            override fun <I> read(indexable: JsonIndexable<I>, index: I): JsonArray {
                return indexable.getArray(index)
            }
        }

        val NUMBER = object : JsonType<Number> {
            override fun <I> write(indexable: JsonIndexable<I>, index: I, value: Number) {
                indexable.setNumber(index, value)
            }

            override fun <I> read(indexable: JsonIndexable<I>, index: I): Number {
                return indexable.getNumber(index)
            }
        }

        val BOOLEAN = object : JsonType<Boolean> {
            override fun <I> write(indexable: JsonIndexable<I>, index: I, value: Boolean) {
                indexable.setBoolean(index, value)
            }

            override fun <I> read(indexable: JsonIndexable<I>, index: I): Boolean {
                return indexable.getBoolean(index)
            }
        }

        val BYTE_ARRAY = object : JsonType<ByteArray> {
            override fun <I> write(indexable: JsonIndexable<I>, index: I, value: ByteArray) {
                indexable.setByteArray(index, value)
            }

            override fun <I> read(indexable: JsonIndexable<I>, index: I): ByteArray {
                return indexable.getByteArray(index)
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
                is ByteArray -> BYTE_ARRAY
                else -> throw IllegalArgumentException("Unknown type: $value")
            } as JsonType<T>
        }
    }

    fun <I> write(indexable: JsonIndexable<I>, index: I, value: T)
    fun <I> read(indexable: JsonIndexable<I>, index: I): T
}