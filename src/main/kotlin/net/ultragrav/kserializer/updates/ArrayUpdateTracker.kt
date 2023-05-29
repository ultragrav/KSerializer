package net.ultragrav.kserializer.updates

import net.ultragrav.kserializer.json.JsonArray
import net.ultragrav.kserializer.serialization.TinySerializer
import net.ultragrav.serializer.GravSerializable
import net.ultragrav.serializer.GravSerializer

class ArrayUpdateTracker() : UpdateTracker<JsonArray>, GravSerializable {
    private val updates = mutableListOf<ArrayUpdate>()

    constructor(serializer: GravSerializer) : this() {
        val size = serializer.readInt()
        for (i in 0 until size) {
            when (serializer.readByte().toInt()) {
                0 -> addUpdate(serializer.readInt(), TinySerializer.read(serializer))
                1 -> setUpdate(serializer.readInt(), TinySerializer.read(serializer))
                2 -> removeUpdate(serializer.readInt(), serializer.readInt())
            }
        }
    }

    internal fun update(update: ArrayUpdate) {
        updates.add(update)
        var index = updates.size - 1
        while (index > 0 && updates[index].shouldCommute(updates[index - 1])) {
            val commuted = updates[index].commute(updates[index - 1])
            updates.subList(index - 1, index + 1).let {
                it.clear()
                if (commuted.isEmpty()) return@update
                it.addAll(commuted)
            }
            index--
        }
    }

    internal fun setUpdate(index: Int, value: Any?) = update(SetUpdate(index, value))
    internal fun addUpdate(index: Int, value: Any?) = update(AddUpdate(index, value))
    internal fun removeUpdate(index: Int, count: Int = 1) = update(RemoveUpdate(index, count))

    fun clear() {
        updates.clear()
    }

    override fun serialize(serializer: GravSerializer) {
        serializer.writeInt(updates.size)
        for (update in updates) {
            when (update) {
                is AddUpdate -> {
                    serializer.writeByte(0)
                    serializer.writeInt(update.index)
                    TinySerializer.write(serializer, update.value)
                }

                is SetUpdate -> {
                    serializer.writeByte(1)
                    serializer.writeInt(update.index)
                    TinySerializer.write(serializer, update.value)
                }

                is RemoveUpdate -> {
                    serializer.writeByte(2)
                    serializer.writeInt(update.index)
                    serializer.writeInt(update.count)
                }
            }
        }
    }

    override fun apply(indexable: JsonArray) {
        for (update in updates) {
            update.apply(indexable)
        }
    }

    override fun toString(): String {
        return "ArrayUpdateTracker(updates=\n${updates.joinToString("\n")}\n)"
    }

    internal abstract class ArrayUpdate(val index: Int) : UpdateTracker.Update<JsonArray> {
        open fun shouldCommute(other: ArrayUpdate): Boolean {
            return other.index >= index
        }

        abstract fun commute(other: ArrayUpdate): List<ArrayUpdate>
    }

    internal class AddUpdate(index: Int, val value: Any?) : ArrayUpdate(index) {
        override fun apply(indexable: JsonArray) {
            indexable.internalAdd(value, index)
        }

        override fun commute(other: ArrayUpdate): List<ArrayUpdate> {
            if (other is SetUpdate) {
                if (other.index < index) return listOf(this, other)
                return listOf(this, SetUpdate(other.index + 1, other.value))
            }
            if (other is AddUpdate) {
                if (other.index < index) return listOf(this, other)
                return listOf(this, AddUpdate(other.index + 1, other.value))
            }
            if (other is RemoveUpdate) {
                if (other.index < index) throw IllegalStateException("Unreachable")
                return listOf(this, RemoveUpdate(other.index + 1, other.count))
            }
            throw IllegalStateException("Invalid update type!")
        }

        override fun toString(): String {
            return "AddUpdate(index=$index, value=$value)"
        }
    }

    internal class RemoveUpdate(index: Int, val count: Int = 1) : ArrayUpdate(index) {
        override fun apply(indexable: JsonArray) {
            if (count == 1) {
                indexable.remove(index)
            } else {
                indexable.backingList.subList(index, index + count).clear()
            }
        }

        override fun commute(other: ArrayUpdate): List<ArrayUpdate> {
            if (other is SetUpdate) {
                if (other.index < index) return listOf(this, other)
                if (other.index < index + count) return listOf(this)
                return listOf(this, SetUpdate(other.index - count, other.value))
            }
            if (other is AddUpdate) {
                if (other.index < index) return listOf(this, other)
                if (other.index < index + count) return listOf(RemoveUpdate(index, count - 1))
                if (other.index == index + count) {
                    return if (count == 1) {
                        listOf(SetUpdate(index, other.value))
                    } else {
                        listOf(RemoveUpdate(index, count - 1), SetUpdate(index, other.value))
                    }
                }
                return listOf(this, AddUpdate(other.index - count, other.value))
            }
            if (other is RemoveUpdate) {
                if (other.index >= index && other.index <= index + count) {
                    val newCount = count + other.count
                    return listOf(RemoveUpdate(index, newCount))
                }
                if (other.index < index) return listOf(this, other) // Unreachable
                return listOf(this, RemoveUpdate(other.index - count, other.count))
            }
            throw IllegalStateException("Invalid update type!")
        }

        override fun toString(): String {
            return "RemoveUpdate(index=$index, count=$count)"
        }
    }

    internal class SetUpdate(index: Int, val value: Any?) : ArrayUpdate(index) {
        override fun apply(indexable: JsonArray) {
            indexable.internalSet(index, value)
        }

        override fun shouldCommute(other: ArrayUpdate): Boolean {
            if (other is RemoveUpdate) return index < other.index
            return super.shouldCommute(other)
        }

        override fun commute(other: ArrayUpdate): List<ArrayUpdate> {
            if (other is SetUpdate) {
                if (other.index == index) return listOf(this)
                return listOf(this, other)
            }
            if (other is AddUpdate) {
                if (other.index > index) return listOf(this, other)
                if (other.index == index) return listOf(AddUpdate(index, value))
                return listOf(SetUpdate(index - 1, value), other)
            }
            if (other is RemoveUpdate) {
                if (other.index <= index) throw IllegalStateException("Unreachable")
                return listOf(this, other)
            }
            throw IllegalStateException("Invalid update type!")
        }

        override fun toString(): String {
            return "SetUpdate(index=$index, value=$value)"
        }
    }
}
