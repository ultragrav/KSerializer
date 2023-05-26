package net.ultragrav.kserializer.json

import java.util.concurrent.locks.ReentrantReadWriteLock

class JsonArray() : JsonIndexable<Int> {
    private val backingList: MutableList<Any?> = mutableListOf()

    private val lock: ReentrantReadWriteLock = ReentrantReadWriteLock()
    private inline fun <T> readLocked(block: () -> T): T {
        try {
            lock.readLock().lock()
            return block()
        } finally {
            lock.readLock().unlock()
        }
    }

    private inline fun <T> writeLocked(block: () -> T): T {
        try {
            lock.writeLock().lock()
            return block()
        } finally {
            lock.writeLock().unlock()
        }
    }

    val size get() = backingList.size

    constructor(list: List<Any?>) : this() {
        backingList.addAll(list)
    }

    override fun getString(key: Int): String = readLocked { return backingList[key] as String }

    override fun setString(key: Int, value: String): Any? = writeLocked {
        growToAccommodate(key)
        return backingList.set(key, value)
    }

    fun addString(value: String, index: Int = -1) = writeLocked {
        if (index == -1)
            backingList.add(value)
        else
            backingList.add(index, value)
    }

    override fun getObject(key: Int): JsonObject = readLocked { return backingList[key] as JsonObject }

    override fun setObject(key: Int, data: JsonObject): Any? = writeLocked {
        growToAccommodate(key)
        return backingList.set(key, data)
    }

    fun addObject(data: JsonObject, index: Int = -1) = writeLocked {
        if (index == -1)
            backingList.add(data)
        else
            backingList.add(index, data)
    }

    override fun getArray(key: Int): JsonArray = readLocked { return backingList[key] as JsonArray }

    override fun setArray(key: Int, array: JsonArray): Any? = writeLocked {
        growToAccommodate(key)
        return backingList.set(key, array)
    }

    fun addArray(array: JsonArray, index: Int = -1) = writeLocked {
        if (index == -1)
            backingList.add(array)
        else
            backingList.add(index, array)
    }

    fun remove(index: Int) = writeLocked { backingList.removeAt(index) }

    fun clear() = writeLocked { backingList.clear() }

    private fun growToAccommodate(index: Int) {
        while (backingList.size <= index) {
            backingList.add(null)
        }
    }
}