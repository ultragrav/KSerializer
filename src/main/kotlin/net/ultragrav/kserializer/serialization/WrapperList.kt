package net.ultragrav.kserializer.serialization

import net.ultragrav.kserializer.Wrapper
import net.ultragrav.kserializer.json.JsonArray
import net.ultragrav.kserializer.json.JsonObject

class WrapperList<T : Wrapper>(private val array: JsonArray, wrapperFactory: (JsonObject) -> T) : MutableList<T> {
    override val size get() = array.size

    private val wrapperList = mutableListOf<T>()

    init {
        for (i in 0 until size) {
            wrapperList.add(wrapperFactory(array.getObject(i)))
        }
    }

    override fun clear() {
        array.clear()
    }

    override fun addAll(elements: Collection<T>): Boolean {
        for (element in elements) {
            add(element)
        }
        return true
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        for (element in elements) {
            add(index, element)
        }
        return true
    }

    override fun add(index: Int, element: T) {
        array.addObject(element.data, index)
        wrapperList.add(index, element)
    }

    override fun add(element: T): Boolean {
        array.addObject(element.data)
        wrapperList.add(element)
        return true
    }

    override fun get(index: Int): T {
        if (index >= size) throw IndexOutOfBoundsException("Index $index is out of bounds for list of size $size")
        return wrapperList[index]
    }

    override fun isEmpty(): Boolean {
        return size == 0
    }

    override fun iterator(): MutableIterator<T> {
        TODO("Not yet implemented")
    }

    override fun listIterator(): MutableListIterator<T> {
        TODO("Not yet implemented")
    }

    override fun listIterator(index: Int): MutableListIterator<T> {
        TODO("Not yet implemented")
    }

    override fun removeAt(index: Int): T {
        if (index >= size) throw IndexOutOfBoundsException("Index $index is out of bounds for list of size $size")
        val value = get(index)
        array.remove(index)
        wrapperList.removeAt(index)
        return value
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        TODO("Not yet implemented")
    }

    override fun set(index: Int, element: T): T {
        if (index >= size) throw IndexOutOfBoundsException("Index $index is out of bounds for list of size $size")

        return wrapperList.set(index, element).also { array.setObject(index, element.data) }
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        TODO("Not yet implemented")
    }

    override fun remove(element: T): Boolean {
        TODO("Not yet implemented")
    }

    override fun lastIndexOf(element: T): Int {
        TODO("Not yet implemented")
    }

    override fun indexOf(element: T): Int {
        TODO("Not yet implemented")
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        TODO("Not yet implemented")
    }

    override fun contains(element: T): Boolean {
        TODO("Not yet implemented")
    }
}