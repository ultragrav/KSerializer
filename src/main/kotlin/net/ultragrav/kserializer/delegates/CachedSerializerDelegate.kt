package net.ultragrav.kserializer.delegates

import net.ultragrav.kserializer.Wrapper
import net.ultragrav.kserializer.serialization.JsonDataSerializer
import kotlin.reflect.KProperty

class CachedSerializerDelegate<T : Any>(val serializer: JsonDataSerializer<T>, val key: String? = null) {
    private lateinit var cachedValue: T

    operator fun getValue(wrapper: Wrapper, property: KProperty<*>): T {
        if (!::cachedValue.isInitialized) {
            cachedValue = serializer.deserialize(wrapper.data, key ?: property.name)
        }
        return cachedValue
    }

    operator fun setValue(wrapper: Wrapper, property: KProperty<*>, value: T) {
        serializer.serialize(wrapper.data, key ?: property.name, value)
        cachedValue = value
    }
}