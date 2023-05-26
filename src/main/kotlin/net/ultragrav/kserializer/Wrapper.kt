package net.ultragrav.kserializer

import net.ultragrav.kserializer.delegates.ListDelegate
import net.ultragrav.kserializer.delegates.SerializerDelegate
import net.ultragrav.kserializer.delegates.WrapperDelegate
import net.ultragrav.kserializer.json.JsonObject
import net.ultragrav.kserializer.serialization.JsonDataSerializer

abstract class Wrapper(internal val data: JsonObject) {


    protected fun <T> serializer(ser: JsonDataSerializer<T>, key: String? = null): SerializerDelegate<T> {
        return SerializerDelegate(ser, key)
    }
    protected fun <T : Wrapper> wrapper(wrapperFactory: (JsonObject) -> T, key: String? = null): WrapperDelegate<T> {
        return WrapperDelegate(wrapperFactory, key)
    }
    protected fun <T> list(ser: JsonDataSerializer<T>, key: String? = null): ListDelegate<T> {
        return ListDelegate(ser, key)
    }

    protected fun string(key: String? = null): SerializerDelegate<String> {
        return serializer(Serializers.STRING, key)
    }
}