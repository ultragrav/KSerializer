package net.ultragrav.kserializer.json

import java.util.concurrent.locks.ReentrantReadWriteLock

class JsonObject : JsonIndexable<String> {
    private val backingMap = mutableMapOf<String, Any>()

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

    override fun getString(key: String): String {
        readLocked { return backingMap[key] as String }
    }
    override fun setString(key: String, value: String): Any? {
        writeLocked { return backingMap.put(key, value) }
    }

    override fun getObject(key: String): JsonObject {
        readLocked { return backingMap[key] as JsonObject }
    }
    override fun setObject(key: String, data: JsonObject): Any? {
        writeLocked { return backingMap.put(key, data) }
    }

    override fun getArray(key: String): JsonArray {
        readLocked { return backingMap[key] as JsonArray }
    }
    override fun setArray(key: String, array: JsonArray): Any? {
        writeLocked { return backingMap.put(key, array) }
    }

    fun contains(key: String): Boolean {
        return backingMap.containsKey(key)
    }
}