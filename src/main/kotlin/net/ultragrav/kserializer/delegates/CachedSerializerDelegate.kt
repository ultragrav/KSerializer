package net.ultragrav.kserializer.delegates

import net.ultragrav.kserializer.Wrapper
import kotlin.reflect.KProperty

class CachedSerializerDelegate<T : Any>(
    private val wrapped: SerializerDelegate<T>,
    val key: String? = null
) {

    private lateinit var cachedValue: T

    operator fun getValue(wrapper: Wrapper, property: KProperty<*>): T {

        if (!::cachedValue.isInitialized) {
            cachedValue = wrapped.getValue(wrapper, property)
        }

        return cachedValue
    }

    operator fun setValue(wrapper: Wrapper, property: KProperty<*>, value: T) {
        cachedValue = value
        wrapped.setValue(wrapper, property, value)
    }
}