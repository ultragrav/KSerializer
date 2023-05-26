package net.ultragrav.kserializer.delegates

import net.ultragrav.kserializer.json.JsonObject
import net.ultragrav.kserializer.Wrapper
import kotlin.reflect.KProperty

class WrapperDelegate<T : Wrapper>(val key: String, private val wrapperFactory: (JsonObject) -> T) {
    private lateinit var cachedWrapper: T

    operator fun getValue(wrapper: Wrapper, property: KProperty<*>): T {
        if (!wrapper.data.contains(key))
            wrapper.data.setObject(key, JsonObject())

        if (!::cachedWrapper.isInitialized) {
            cachedWrapper = wrapperFactory(wrapper.data.getObject(key))
        }
        return cachedWrapper
    }

    operator fun setValue(wrapper: Wrapper, property: KProperty<*>, value: T) {
        cachedWrapper = value
        wrapper.data.setObject(key, value.data)
    }
}