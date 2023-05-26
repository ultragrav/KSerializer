package net.ultragrav.kserializer.json

class JsonObject : JsonIndexable<String> {
    private val backingMap = mutableMapOf<String, Any>()

    override fun getString(key: String): String {
        return backingMap[key] as String
    }
    override fun setString(key: String, value: String): Any? {
        return backingMap.put(key, value)
    }

    override fun getObject(key: String): JsonObject {
        return backingMap[key] as JsonObject
    }
    override fun setObject(key: String, data: JsonObject): Any? {
        return backingMap.put(key, data)
    }

    override fun getArray(key: String): JsonArray {
        return backingMap[key] as JsonArray
    }
    override fun setArray(key: String, array: JsonArray): Any? {
        return backingMap.put(key, array)
    }

    fun contains(key: String): Boolean {
        return backingMap.containsKey(key)
    }
}