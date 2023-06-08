package net.ultragrav.kserializer.delegates

import net.ultragrav.kserializer.Wrapper
import net.ultragrav.kserializer.json.JsonArray
import net.ultragrav.kserializer.json.JsonObject
import net.ultragrav.kserializer.serialization.WrapperList
import kotlin.reflect.KProperty

class WrapperListDelegate<T : Wrapper>(val wrapperFactory: (JsonObject) -> T, val key: String? = null) {
    private lateinit var cachedList: WrapperList<T>

    operator fun getValue(wrapper: Wrapper, property: KProperty<*>): MutableList<T> {
        val k = key ?: property.name
        if (!wrapper.data.contains(k))
            wrapper.data.setArray(k, JsonArray())

        if (!::cachedList.isInitialized) {
            cachedList = WrapperList(wrapper.data.getArray(k), wrapperFactory)
        }
        return cachedList
    }

    operator fun setValue(wrapper: Wrapper, property: KProperty<*>, value: List<T>) {
        val arr = JsonArray(value)
        cachedList = WrapperList(arr, wrapperFactory)
        wrapper.data.setArray(key ?: property.name, arr)
    }
}