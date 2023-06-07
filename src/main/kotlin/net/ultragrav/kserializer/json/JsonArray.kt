package net.ultragrav.kserializer.json

import net.ultragrav.kserializer.updates.ArrayUpdateTracker
import java.util.concurrent.locks.ReentrantReadWriteLock

open class JsonArray(initialSize: Int = 8) : JsonIndexable<Int> {
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

    internal val backingList: MutableList<Any?> = ArrayList(initialSize)

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

    constructor(list: List<Any?>) : this() {
        backingList.addAll(list)
    }

    override fun getString(key: Int): String = readLocked { return backingList[key] as String }
    override fun setString(key: Int, value: String): Any? = internalSet(key, value)
    fun addString(value: String, index: Int = -1) = internalAdd(value, index)

    override fun getObject(key: Int): JsonObject = readLocked { return backingList[key] as JsonObject }
    override fun setObject(key: Int, data: JsonObject): Any? = internalSet(key, data)
    fun addObject(data: JsonObject, index: Int = -1) = internalAdd(data, index)

    override fun getArray(key: Int): JsonArray = readLocked { return backingList[key] as JsonArray }
    override fun setArray(key: Int, array: JsonArray): Any? = internalSet(key, array)
    fun addArray(array: JsonArray, index: Int = -1) = internalAdd(array, index)

    override fun getNumber(key: Int): Number = readLocked { return backingList[key] as Number }
    override fun setNumber(key: Int, number: Number): Any? = internalSet(key, number)
    fun addNumber(number: Number, index: Int = -1) = internalAdd(number, index)

    override fun getBoolean(key: Int): Boolean = readLocked { return backingList[key] as Boolean }
    override fun setBoolean(key: Int, boolean: Boolean): Any? = internalSet(key, boolean)
    fun addBoolean(boolean: Boolean, index: Int = -1) = internalAdd(boolean, index)

    override fun getByteArray(key: Int): ByteArray = readLocked { return backingList[key] as ByteArray }
    override fun setByteArray(key: Int, byteArray: ByteArray): Any? = internalSet(key, byteArray)
    fun addByteArray(byteArray: ByteArray, index: Int = -1) = internalAdd(byteArray, index)

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
        if (it is JsonObject) {
            it.asMap()
        } else if (it is JsonArray) {
            it.asList()
        } else {
            it
        }
    }

    override fun toString(): String {
        // Convert to JSON string
        val builder = StringBuilder()
        builder.append("[")
        for (i in 0 until size) {
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
}