package net.ultragrav.kserializer.json

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.ultragrav.kserializer.serialization.TinySerializer
import net.ultragrav.serializer.GravSerializable
import net.ultragrav.serializer.GravSerializer
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock

class JsonObject : JsonIndexable<String>, GravSerializable {
    private val backingMap: LinkedHashMap<String, Any>

    override val size get() = readLocked { backingMap.size }
    override val keys get() = readLocked { backingMap.keys }

    @Transient
    private val lock: ReentrantReadWriteLock = ReentrantReadWriteLock()

    constructor() : this(8)
    constructor(initialCapacity: Int) {
        backingMap = LinkedHashMap(initialCapacity)
    }

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

    private fun <T : Any> internalSet(key: String, obj: T): Any? =
        writeLocked { return backingMap.put(key, obj) }

    private fun <T : Any> internalGet(key: String): T {
        @Suppress("UNCHECKED_CAST")
        return readLocked { backingMap[key] as T }
    }

    override fun getString(key: String): String = internalGet(key)
    override fun setString(key: String, value: String): Any? = internalSet(key, value)

    override fun getObject(key: String): JsonObject = internalGet(key)
    override fun setObject(key: String, data: JsonObject): Any? = internalSet(key, data)

    override fun getArray(key: String): JsonArray = internalGet(key)
    override fun setArray(key: String, array: JsonArray): Any? = internalSet(key, array)

    override fun getNumber(key: String): Double = internalGet(key)
    override fun setNumber(key: String, number: Double): Any? = internalSet(key, number)

    override fun getBoolean(key: String): Boolean = internalGet(key)
    override fun setBoolean(key: String, boolean: Boolean): Any? = internalSet(key, boolean)

    override fun getBinary(key: String): BsonBinary = internalGet(key)
    override fun setBinary(key: String, binary: BsonBinary): Any? = internalSet(key, binary)

    override fun getDate(key: String): Date = internalGet(key)
    override fun setDate(key: String, date: Date) = internalSet(key, date)

    override fun getInt(key: String): Int = internalGet(key)
    override fun setInt(key: String, int: Int): Any? = internalSet(key, int)

    override fun getLong(key: String): Long = internalGet(key)
    override fun setLong(key: String, long: Long): Any? = internalSet(key, long)

    override fun type(key: String): JsonType<*> = readLocked {
        JsonType.of(backingMap[key])
    }

    override fun <R> get(key: String): R = readLocked {
        @Suppress("UNCHECKED_CAST")
        backingMap[key] as R
    }

    override fun contains(key: String): Boolean {
        return backingMap.containsKey(key)
    }

    override fun copy(): JsonObject = readLocked {
        val copy = JsonObject(backingMap.size)
        backingMap.forEach { (key, value) ->
            copy.backingMap[key] = if (value is JsonIndexable<*>) value.copy() else value
        }
        return copy
    }

    override fun remove(key: String): Any? = writeLocked { backingMap.remove(key) }

    override fun clear() = writeLocked { backingMap.clear() }

    fun asMap(): Map<String, Any?> = backingMap.mapValues { (_, value) ->
        when (value) {
            is JsonArray -> {
                value.asList()
            }

            is JsonObject -> {
                value.asMap()
            }

            else -> {
                value
            }
        }
    }

    override val defaultKey = "value"

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
                is BsonBinary -> builder.append(value.toString())
                else -> throw IllegalArgumentException("Invalid value type: ${value::class.java}")
            }
        }
        builder.append("}")
        return builder.toString()
    }

    override fun serialize(serializer: GravSerializer) = TinySerializer.write(serializer, this)

    companion object {
        @JvmStatic
        fun deserialize(serializer: GravSerializer): JsonObject = TinySerializer.read(serializer) as JsonObject
    }
}