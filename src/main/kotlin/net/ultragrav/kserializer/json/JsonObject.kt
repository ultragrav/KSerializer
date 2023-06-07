package net.ultragrav.kserializer.json

import java.util.concurrent.locks.ReentrantReadWriteLock

open class JsonObject(initialCapacity: Int = 8) : JsonIndexable<String> {
    internal val backingMap = LinkedHashMap<String, Any>(initialCapacity)

    override val size get() = readLocked { backingMap.size }

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

    protected open fun <T : Any> internalSet(key: String, obj: T): Any? =
        writeLocked { return backingMap.put(key, obj) }

    protected open fun <T : Any> internalGet(key: String): T {
        @Suppress("UNCHECKED_CAST")
        return readLocked { backingMap[key] as T }
    }

    override fun getString(key: String): String = internalGet(key)
    override fun setString(key: String, value: String): Any? = internalSet(key, value)

    override fun getObject(key: String): JsonObject = internalGet(key)
    override fun setObject(key: String, data: JsonObject): Any? = internalSet(key, data)

    override fun getArray(key: String): JsonArray = internalGet(key)
    override fun setArray(key: String, array: JsonArray): Any? = internalSet(key, array)

    override fun getNumber(key: String): Number = internalGet(key)
    override fun setNumber(key: String, number: Number): Any? = internalSet(key, number)

    override fun getBoolean(key: String): Boolean = internalGet(key)
    override fun setBoolean(key: String, boolean: Boolean): Any? = internalSet(key, boolean)

    override fun getByteArray(key: String): ByteArray = internalGet(key)
    override fun setByteArray(key: String, byteArray: ByteArray): Any? = internalSet(key, byteArray)

    fun contains(key: String): Boolean {
        return backingMap.containsKey(key)
    }

    override fun remove(key: String): Any? = writeLocked { backingMap.remove(key) }

    override fun clear() = writeLocked { backingMap.clear() }

    fun asMap(): Map<String, Any?> = backingMap.mapValues {
        if (it is JsonArray) {
            it.asList()
        } else if (it is JsonObject) {
            it.asMap()
        } else {
            it
        }
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