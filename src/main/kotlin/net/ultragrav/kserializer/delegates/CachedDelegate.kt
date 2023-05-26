package net.ultragrav.kserializer.delegates

import net.ultragrav.kserializer.Wrapper
import kotlin.reflect.KProperty

class CachedDelegate<T : Any>(val getter: (Wrapper, KProperty<*>) -> T, val setter: (Wrapper, KProperty<*>, T) -> Unit) {
    private lateinit var cachedValue: T

    operator fun getValue(wrapper: Wrapper, property: KProperty<*>): T {
        if (!::cachedValue.isInitialized) {
            cachedValue = getter(wrapper, property)
        }
        return cachedValue
    }

    operator fun setValue(wrapper: Wrapper, property: KProperty<*>, value: T) {
        setter(wrapper, property, value)
        cachedValue = value
    }
}