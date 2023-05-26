package net.ultragrav.kserializer

import net.ultragrav.kserializer.delegates.SerializerDelegate
import net.ultragrav.kserializer.delegates.WrapperDelegate
import net.ultragrav.kserializer.json.JsonObject
import net.ultragrav.kserializer.serialization.JsonDataSerializer

abstract class Wrapper(internal val data: JsonObject) {


    protected fun <T> serializer(key: String, ser: JsonDataSerializer<T>): SerializerDelegate<T> {
        return SerializerDelegate(key, ser)
    }
    protected fun <T : Wrapper> wrapper(key: String, wrapperFactory: (JsonObject) -> T): WrapperDelegate<T> {
        return WrapperDelegate(key, wrapperFactory)
    }

    protected fun string(key: String): SerializerDelegate<String> {
        return serializer(key, Serializers.STRING)
    }
}