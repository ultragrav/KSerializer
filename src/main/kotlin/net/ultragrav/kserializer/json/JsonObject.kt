package net.ultragrav.kserializer.json

import java.util.concurrent.locks.ReentrantReadWriteLock

open class JsonObject(initialCapacity: Int = 0) : JsonIndexable<String> {
    internal val backingMap = LinkedHashMap<String, Any>(initialCapacity)

    val size get() = readLocked { backingMap.size }

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

    override fun getString(key: String): String = readLocked { return backingMap[key] as String }
    override fun setString(key: String, value: String): Any? = writeLocked { return backingMap.put(key, value) }

    override fun getObject(key: String): JsonObject = readLocked { return backingMap[key] as JsonObject }
    override fun setObject(key: String, data: JsonObject): Any? = writeLocked { return backingMap.put(key, data) }

    override fun getArray(key: String): JsonArray = readLocked { return backingMap[key] as JsonArray }
    override fun setArray(key: String, array: JsonArray): Any? = writeLocked { return backingMap.put(key, array) }

    override fun getNumber(key: String): Number = readLocked { return backingMap[key] as Number }
    override fun setNumber(key: String, number: Number): Any? = writeLocked { return backingMap.put(key, number) }

    override fun getBoolean(key: String): Boolean = readLocked { return backingMap[key] as Boolean }
    override fun setBoolean(key: String, boolean: Boolean): Any? = writeLocked { return backingMap.put(key, boolean) }

    override fun getByteArray(key: String): ByteArray = readLocked { return backingMap[key] as ByteArray }
    override fun setByteArray(key: String, byteArray: ByteArray): Any? = writeLocked { return backingMap.put(key, byteArray) }

    fun contains(key: String): Boolean {
        return backingMap.containsKey(key)
    }

    override fun toString(): String {
        // Convert to JSON string
        val builder = StringBuilder()
        builder.append("{")
        var first = true
        for ((key, value) in backingMap) {
            if (!first) {
                builder.append(",")
            }
            first = false
            builder.append("\"$key\":")
            when (value) {
                is String -> builder.append("\"$value\"")
                is JsonObject -> builder.append(value.toString())
                is JsonArray -> builder.append(value.toString())
                is Number -> builder.append(value.toString())
                is Boolean -> builder.append(value.toString())
                else -> throw IllegalArgumentException("Invalid value type: ${value::class.java}")
            }
        }
        builder.append("}")
        return builder.toString()
    }
}