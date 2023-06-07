package net.ultragrav.kserializer.delegates

import net.ultragrav.kserializer.serialization.JsonDataSerializer
import net.ultragrav.kserializer.Wrapper
import net.ultragrav.kserializer.json.JsonArray
import net.ultragrav.kserializer.serialization.SerializedList
import kotlin.reflect.KProperty

class ListDelegate<T>(private val serializer: JsonDataSerializer<T>, val key: String? = null) {
    private lateinit var cachedList: SerializedList<T>

    operator fun getValue(wrapper: Wrapper, property: KProperty<*>): MutableList<T> {
        val k = key ?: property.name
        if (!wrapper.data.contains(k))
            wrapper.data.setArray(k, JsonArray())

        if (!::cachedList.isInitialized) {
            cachedList = SerializedList(wrapper.data.getArray(k), serializer)
        }

        return cachedList
    }

    operator fun setValue(wrapper: Wrapper, property: KProperty<*>, value: List<T>) {
        val arr = JsonArray(value)
        cachedList = SerializedList(arr, serializer)
        wrapper.data.setArray(key ?: property.name, arr)
    }

    fun cache() = CachedListDelegate(serializer, key)
}