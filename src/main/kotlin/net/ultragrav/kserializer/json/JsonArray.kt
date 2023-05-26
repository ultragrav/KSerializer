package net.ultragrav.kserializer.json

class JsonArray() : JsonIndexable<Int> {
    private val backingList: MutableList<Any?> = mutableListOf()

    val size get() = backingList.size

    constructor(list: List<Any?>) : this() {
        backingList.addAll(list)
    }

    override fun getString(key: Int): String {
        return backingList[key] as String
    }

    override fun setString(key: Int, value: String): Any? {
        growToAccommodate(key)
        return backingList.set(key, value)
    }

    fun addString(value: String, index: Int = -1) {
        if (index == -1)
            backingList.add(value)
        else
            backingList.add(index, value)
    }

    override fun getObject(key: Int): JsonObject {
        return backingList[key] as JsonObject
    }

    override fun setObject(key: Int, data: JsonObject): Any? {
        growToAccommodate(key)
        return backingList.set(key, data)
    }

    fun addObject(data: JsonObject, index: Int = -1) {
        if (index == -1)
            backingList.add(data)
        else
            backingList.add(index, data)
    }

    override fun getArray(key: Int): JsonArray {
        return backingList[key] as JsonArray
    }

    override fun setArray(key: Int, array: JsonArray): Any? {
        growToAccommodate(key)
        return backingList.set(key, array)
    }

    fun addArray(array: JsonArray, index: Int = -1) {
        if (index == -1)
            backingList.add(array)
        else
            backingList.add(index, array)
    }

    fun remove(index: Int) {
        backingList.removeAt(index)
    }

    fun clear() {
        backingList.clear()
    }

    private fun growToAccommodate(index: Int) {
        while (backingList.size <= index) {
            backingList.add(null)
        }
    }
}