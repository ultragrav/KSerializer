package net.ultragrav.kserializer.json

import net.ultragrav.kserializer.serialization.TinySerializer
import net.ultragrav.kserializer.updates.ArrayUpdateTracker
import net.ultragrav.serializer.GravSerializable
import net.ultragrav.serializer.GravSerializer
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.collections.ArrayList

open class JsonArray(initialSize: Int = 8) : JsonIndexable<Int>, GravSerializable {
    var trackingUpdates = false
        set(value) {
            if (value) {
                if (updateTracker == null) {
                    updateTracker = ArrayUpdateTracker()
                }
            } else {
                updateTracker = null
            }
            field = value
        }

    internal var backingList: MutableList<Any?> = ArrayList(initialSize)

    internal var updateTracker: ArrayUpdateTracker? = null

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

    override val size get() = backingList.size
    override val keys get() = backingList.indices

    constructor(list: List<Any?>) : this() {
        backingList.addAll(list)
    }

    fun addNull(index: Int = -1) = internalAdd(null, index)

    override fun getString(key: Int): String = readLocked { return backingList[key] as String }
    override fun setString(key: Int, value: String): Any? = internalSet(key, value)
    fun addString(value: String, index: Int = -1) = internalAdd(value, index)

    override fun getObject(key: Int): JsonObject = readLocked { return backingList[key] as JsonObject }
    override fun setObject(key: Int, data: JsonObject): Any? = internalSet(key, data)
    fun addObject(data: JsonObject, index: Int = -1) = internalAdd(data, index)

    override fun getArray(key: Int): JsonArray = readLocked { return backingList[key] as JsonArray }
    override fun setArray(key: Int, array: JsonArray): Any? = internalSet(key, array)
    fun addArray(array: JsonArray, index: Int = -1) = internalAdd(array, index)

    override fun getNumber(key: Int): Double = readLocked { return backingList[key] as Double }
    override fun setNumber(key: Int, number: Double): Any? = internalSet(key, number)
    fun addNumber(number: Double, index: Int = -1) = internalAdd(number, index)

    override fun getBoolean(key: Int): Boolean = readLocked { return backingList[key] as Boolean }
    override fun setBoolean(key: Int, boolean: Boolean): Any? = internalSet(key, boolean)
    fun addBoolean(boolean: Boolean, index: Int = -1) = internalAdd(boolean, index)

    override fun getBinary(key: Int): BsonBinary = readLocked { return backingList[key] as BsonBinary }
    override fun setBinary(key: Int, binary: BsonBinary): Any? = internalSet(key, binary)
    fun addBinary(binary: BsonBinary, index: Int = -1) = internalAdd(binary, index)
    fun addBinary(type: BsonBinaryType, value: ByteArray, index: Int = -1) = addBinary(BsonBinary(type, value), index)
    fun addBinary(value: ByteArray, index: Int = -1) = addBinary(BsonBinaryType.GENERIC, value, index)

    override fun getDate(key: Int): Date = readLocked { return backingList[key] as Date }
    override fun setDate(key: Int, date: Date): Any? = internalSet(key, date)
    fun addDate(value: Date, index: Int = -1) = internalAdd(value, index)

    override fun getInt(key: Int): Int = readLocked { return backingList[key] as Int }
    override fun setInt(key: Int, int: Int): Any? = internalSet(key, int)
    fun addInt(value: Int, index: Int = -1) = internalAdd(value, index)

    override fun getLong(key: Int): Long = readLocked { return backingList[key] as Long }
    override fun setLong(key: Int, long: Long): Any? = internalSet(key, long)
    fun addLong(value: Long, index: Int = -1) = internalAdd(value, index)

    override fun type(key: Int): JsonType<*> {
        return JsonType.of(backingList[key])
    }

    override fun <R> get(key: Int): R = readLocked {
        @Suppress("UNCHECKED_CAST")
        backingList[key] as R
    }

    fun add(value: Any?, index: Int = -1) = JsonType.of(value)
        .write(this, if (index == -1) size else index, value)

    override fun remove(key: Int) = writeLocked {
        backingList.removeAt(key)
        if (trackingUpdates) {
            updateTracker!!.removeUpdate(key)
        }
    }

    override fun clear() = writeLocked {
        if (trackingUpdates) {
            updateTracker!!.clear()
            updateTracker!!.update(ArrayUpdateTracker.RemoveUpdate(0, size))
        }
        backingList.clear()
    }

    override fun contains(key: Int): Boolean {
        return key in 0..<size
    }


    internal fun internalSet(key: Int, value: Any?) = writeLocked {
        growToAccommodate(key)
        if (trackingUpdates) {
            updateTracker!!.setUpdate(key, value)
        }
        backingList[key] = value
    }

    internal fun internalAdd(value: Any?, index: Int = -1) = writeLocked {
        if (index == -1) {
            if (trackingUpdates) {
                updateTracker!!.addUpdate(backingList.size, value)
            }
            backingList.add(value)
        } else {
            if (trackingUpdates) {
                updateTracker!!.addUpdate(index, value)
            }
            backingList.add(index, value)
        }
    }

    private fun growToAccommodate(index: Int) {
        while (backingList.size <= index) {
            backingList.add(null)
        }
    }

    fun asList(): List<Any?> = backingList.map {
        when (it) {
            is JsonObject -> it.asMap()
            is JsonArray -> it.asList()
            else -> it
        }
    }

    override fun toString(): String {
        // Convert to JSON string
        val builder = StringBuilder()
        builder.append("[")
        for (i in 0..<size) {
            when (val value = backingList[i]) {
                null -> builder.append("null")
                is String -> builder.append("\"$value\"")
                is JsonObject -> builder.append(value.toString())
                is JsonArray -> builder.append(value.toString())
                is Number -> builder.append(value.toString())
                is Boolean -> builder.append(value.toString())
                else -> throw IllegalArgumentException("Invalid value type: ${value::class.java}")
            }
            if (i != size - 1) {
                builder.append(",")
            }
        }
        builder.append("]")
        return builder.toString()
    }

    override fun serialize(serializer: GravSerializer) = TinySerializer.write(serializer, this)

    companion object {
        fun deserialize(serializer: GravSerializer): JsonArray = TinySerializer.read(serializer) as JsonArray
    }
}