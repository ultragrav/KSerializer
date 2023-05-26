package net.ultragrav.kserializer.delegates

import net.ultragrav.kserializer.serialization.JsonDataSerializer
import net.ultragrav.kserializer.Wrapper
import net.ultragrav.kserializer.json.JsonArray
import net.ultragrav.kserializer.serialization.SerializedList
import kotlin.reflect.KProperty

class ListDelegate<T>(val key: String, val serializer: JsonDataSerializer<T>) {
    private lateinit var cachedList: SerializedList<T>

    operator fun getValue(wrapper: Wrapper, property: KProperty<*>): MutableList<T> {
        if (!wrapper.data.contains(key))
            wrapper.data.setArray(key, JsonArray())

        if (!::cachedList.isInitialized) {
            cachedList = SerializedList(wrapper.data.getArray(key), serializer)
        }
        return cachedList
    }

    operator fun setValue(wrapper: Wrapper, property: KProperty<*>, value: List<T>) {
        val arr = JsonArray(value)
        cachedList = SerializedList(arr, serializer)
        wrapper.data.setArray(key, arr)
    }
}