package net.ultragrav.kserializer

import net.ultragrav.kserializer.delegates.ListDelegate
import net.ultragrav.kserializer.delegates.SerializerDelegate
import net.ultragrav.kserializer.delegates.WrapperDelegate
import net.ultragrav.kserializer.json.JsonObject
import net.ultragrav.kserializer.serialization.JsonDataSerializer
import kotlin.reflect.KClass

abstract class Wrapper(internal val data: JsonObject) {


    protected fun <T : Any> serializer(ser: JsonDataSerializer<T>, key: String? = null): SerializerDelegate<T> {
        return SerializerDelegate(ser, key)
    }
    protected fun <T : Wrapper> wrapper(wrapperFactory: (JsonObject) -> T, key: String? = null): WrapperDelegate<T> {
        return WrapperDelegate(wrapperFactory, key)
    }
    protected fun <T> list(ser: JsonDataSerializer<T>, key: String? = null): ListDelegate<T> {
        return ListDelegate(ser, key)
    }
    protected fun <T : Enum<T>> enum(enumClass: KClass<T>, key: String? = null): SerializerDelegate<T> {
        return serializer(Serializers.enum(enumClass), key)
    }

    protected fun string(key: String? = null): SerializerDelegate<String> {
        return serializer(Serializers.STRING, key)
    }
}