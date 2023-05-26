package net.ultragrav.kserializer

import kotlin.reflect.KProperty

class SerializerDelegate<T>(val key: String, val serializer: JsonDataSerializer<T>) {
    operator fun getValue(wrapper: Wrapper, property: KProperty<*>): T {
        return serializer.deserialize(wrapper.data, key)
    }

    operator fun setValue(wrapper: Wrapper, property: KProperty<*>, value: T) {
        serializer.serialize(wrapper.data, key, value)
    }
}