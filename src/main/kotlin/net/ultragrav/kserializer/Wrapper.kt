package net.ultragrav.kserializer

import net.ultragrav.kserializer.delegates.SerializerDelegate
import net.ultragrav.kserializer.delegates.WrapperDelegate

abstract class Wrapper(internal val data: JsonData) {


    protected fun <T> serializer(key: String, ser: JsonDataSerializer<T>): SerializerDelegate<T> {
        return SerializerDelegate(key, ser)
    }
    protected fun <T : Wrapper> wrapper(key: String, wrapperFactory: (JsonData) -> T): WrapperDelegate<T> {
        return WrapperDelegate(key, wrapperFactory)
    }

    protected fun string(key: String): SerializerDelegate<String> {
        return SerializerDelegate(key, Serializers.STRING)
    }
}