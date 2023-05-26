package net.ultragrav.kserializer.delegates

import net.ultragrav.kserializer.Wrapper
import net.ultragrav.kserializer.serialization.JsonDataSerializer
import kotlin.reflect.KProperty

class SerializerDelegate<T : Any>(val serializer: JsonDataSerializer<T>, val key: String? = null) {
    operator fun getValue(wrapper: Wrapper, property: KProperty<*>): T {
        return serializer.deserialize(wrapper.data, key ?: property.name)
    }

    operator fun setValue(wrapper: Wrapper, property: KProperty<*>, value: T) {
        serializer.serialize(wrapper.data, key ?: property.name, value)
    }

    fun cache(): CachedDelegate<T> = CachedDelegate(
        { wrapper, property -> getValue(wrapper, property) },
        { wrapper, property, value -> setValue(wrapper, property, value) }
    )
}