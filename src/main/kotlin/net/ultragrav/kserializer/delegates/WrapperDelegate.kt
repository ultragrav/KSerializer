package net.ultragrav.kserializer.delegates

import net.ultragrav.kserializer.Wrapper
import net.ultragrav.kserializer.json.JsonObject
import kotlin.reflect.KProperty

class WrapperDelegate<T : Wrapper>(private val wrapperFactory: (JsonObject) -> T, val key: String? = null) {
    private lateinit var cachedWrapper: T

    operator fun getValue(wrapper: Wrapper, property: KProperty<*>): T {
        val k = key ?: property.name
        if (!wrapper.data.contains(k))
            wrapper.data.setObject(k, JsonObject())

        if (!::cachedWrapper.isInitialized) {
            cachedWrapper = wrapperFactory(wrapper.data.getObject(k))
        }
        return cachedWrapper
    }

    operator fun setValue(wrapper: Wrapper, property: KProperty<*>, value: T) {
        cachedWrapper = value
        wrapper.data.setObject(key ?: property.name, value.data)
    }
}