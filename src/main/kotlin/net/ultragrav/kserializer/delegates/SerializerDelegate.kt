package net.ultragrav.kserializer.delegates

import net.ultragrav.kserializer.Wrapper
import net.ultragrav.kserializer.serialization.JsonDataSerializer
import kotlin.reflect.KProperty

class SerializerDelegate<T : Any>(
    val serializer: JsonDataSerializer<T>,
    val key: String? = null,
    val initial: T
) {

    operator fun getValue(wrapper: Wrapper, property: KProperty<*>): T {
        val key = key ?: property.name
        if (!wrapper.data.contains(key)) serializer.serialize(wrapper.data, key, initial)
        return serializer.deserialize(wrapper.data, key)
    }

    operator fun setValue(wrapper: Wrapper, property: KProperty<*>, value: T) {
        serializer.serialize(wrapper.data, key ?: property.name, value)
    }

    fun cache() = CachedSerializerDelegate(this, key)
}