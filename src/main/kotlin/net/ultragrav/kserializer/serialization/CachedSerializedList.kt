package net.ultragrav.kserializer.serialization

import net.ultragrav.kserializer.json.JsonArray

class CachedSerializedList<T>(private val array: JsonArray, private val serializer: JsonDataSerializer<T>) : MutableList<T> {
    override val size get() = array.size
    private val cache = ArrayList<T?>(size)

    override fun clear() {
        array.clear()
        cache.clear()
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
        serializer.serializeAdd(array, element, index)
        cache.add(index, null)
    }

    override fun add(element: T): Boolean {
        serializer.serializeAdd(array, element)
        cache.add(null)
        return true
    }

    override fun get(index: Int): T {
        if (index >= size) throw IndexOutOfBoundsException("Index $index is out of bounds for list of size $size")

        return cache[index] ?: serializer.deserialize(array, index).also { cache[index] = it }
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
        cache.removeAt(index)
        return value
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        TODO("Not yet implemented")
    }

    override fun set(index: Int, element: T): T {
        if (index >= size) throw IndexOutOfBoundsException("Index $index is out of bounds for list of size $size")

        cache[index] = element
        @Suppress("UNCHECKED_CAST") // If the backing json array is valid, this cast is safe
        return serializer.serialize(array, index, element) as T
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