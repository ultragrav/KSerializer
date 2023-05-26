package net.ultragrav.kserializer.delegates

import net.ultragrav.kserializer.JsonData
import net.ultragrav.kserializer.Wrapper
import kotlin.reflect.KProperty

class WrapperDelegate<T : Wrapper>(val key: String, private val wrapperFactory: (JsonData) -> T) {
    private lateinit var cachedWrapper: T

    operator fun getValue(wrapper: Wrapper, property: KProperty<*>): T {
        if (!::cachedWrapper.isInitialized) {
            cachedWrapper = wrapperFactory(wrapper.data.getData(key))
        }
        return cachedWrapper
    }
}