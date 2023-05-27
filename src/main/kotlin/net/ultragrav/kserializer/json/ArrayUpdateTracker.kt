package net.ultragrav.kserializer.json

import java.util.*
import java.util.concurrent.ThreadLocalRandom

class ArrayUpdateTracker {
    internal val updates = mutableListOf<Update>()

    internal fun update(update: Update): Update {
        updates.add(update)
        var index = updates.size - 1
        while (index > 0 && updates[index].shouldCommute(updates[index - 1])) {
            val commuted = updates[index].commute(updates[index - 1])
            updates.subList(index - 1, index + 1).let {
                it.clear()
                if (commuted.isEmpty()) return@update update
                it.addAll(commuted)
            }
            index--
        }
        return update
    }

    fun apply(array: JsonArray) {
        for (update in updates) {
            update.apply(array)
        }
    }

    override fun toString(): String {
        return "ArrayUpdateTracker(updates=\n${updates.joinToString("\n")}\n)"
    }

    internal abstract class Update(val index: Int) {
        open fun shouldCommute(other: Update): Boolean {
            return other.index >= index
        }

        abstract fun apply(array: JsonArray)
        abstract fun commute(other: Update): List<Update>
    }

    internal class AddUpdate(index: Int, val value: Any?) : Update(index) {
        override fun apply(array: JsonArray) {
            array.internalAdd(value, index)
        }

        override fun commute(other: Update): List<Update> {
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

    internal class RemoveUpdate(index: Int, val count: Int = 1) : Update(index) {
        override fun apply(array: JsonArray) {
            if (count == 1) {
                array.remove(index)
            } else {
                array.backingList.subList(index, index + count).clear()
            }
        }

        override fun commute(other: Update): List<Update> {
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

    internal class SetUpdate(index: Int, val value: Any?) : Update(index) {
        override fun apply(array: JsonArray) {
            array.internalSet(index, value)
        }

        override fun shouldCommute(other: Update): Boolean {
            if (other is RemoveUpdate) return index < other.index
            return super.shouldCommute(other)
        }

        override fun commute(other: Update): List<Update> {
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

fun copy(arr: JsonArray): JsonArray {
    val copy = JsonArray()
    copy.backingList.addAll(arr.backingList)
    return copy
}

fun compare(arr1: JsonArray, arr2: JsonArray): Boolean {
    if (arr1.size != arr2.size) return false
    for (i in 0 until arr1.size) {
        if (arr1.backingList[i] != arr2.backingList[i]) return false
    }
    return true
}

fun main() {
    val tracker = ArrayUpdateTracker()

    val seed = ThreadLocalRandom.current().nextLong()
    val random = Random(seed)


    val list = JsonArray()
    val originalList = JsonArray()
    for (i in 0..200) {
        val value = random.nextInt(100)
        list.addNumber(value)
        originalList.addNumber(value)
    }

//    println("Original: ${originalList.backingList.joinToString(", ")}")

    val startTime = System.nanoTime()
    for (i in 0..1000000) {
        val rand = random.nextDouble()
        val update = if (list.size == 0 || rand < 0.33) {
            // Add
            val value = random.nextInt(100)
            val index = random.nextInt(list.size + 1)
            list.addNumber(value, index)
            tracker.update(ArrayUpdateTracker.AddUpdate(index, value))
        } else if (rand < 0.66) {
            // Set
            val value = random.nextInt(100)
            val index = random.nextInt(list.size)
            list.setNumber(index, value)
            tracker.update(ArrayUpdateTracker.SetUpdate(index, value))
        } else {
            // Remove
            val index = random.nextInt(list.size)
            list.remove(index)
            tracker.update(ArrayUpdateTracker.RemoveUpdate(index))
        }

        println(update)

        val copy = copy(originalList)
        tracker.apply(copy)
        println("List: ${list.backingList.joinToString(", ")}")
        println("Copy: ${copy.backingList.joinToString(", ")}")
        println(tracker)
        if (!compare(list, copy)) {
            println("LISTS DIFFER!!!!!")
        }
        println()
    }
    val endTime = System.nanoTime()

    // Compare the two lists
    println(tracker.updates.size)
    println("Time: ${(endTime - startTime) / 1000000.0}ms")
    tracker.apply(originalList)

    println(list.size)

//    println(tracker)

    for (i in 0 until list.size) {
        if (list.getNumber(i) != originalList.getNumber(i)) {
            println("Lists differ at index $i: ${list.getNumber(i)} != ${originalList.getNumber(i)}")
            break
        }
    }
}
