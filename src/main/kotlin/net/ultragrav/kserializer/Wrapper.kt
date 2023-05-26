package net.ultragrav.kserializer

import net.ultragrav.kserializer.delegates.ListDelegate
import net.ultragrav.kserializer.delegates.SerializerDelegate
import net.ultragrav.kserializer.delegates.WrapperDelegate
import net.ultragrav.kserializer.json.JsonObject
import net.ultragrav.kserializer.serialization.JsonDataSerializer
import kotlin.reflect.KClass

abstract class Wrapper(val data: JsonObject) {
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
    protected fun int(key: String? = null): SerializerDelegate<Int> {
        return serializer(Serializers.INT, key)
    }
    protected fun long(key: String? = null): SerializerDelegate<Long> {
        return serializer(Serializers.LONG, key)
    }
    protected fun double(key: String? = null): SerializerDelegate<Double> {
        return serializer(Serializers.DOUBLE, key)
    }
    protected fun float(key: String? = null): SerializerDelegate<Float> {
        return serializer(Serializers.FLOAT, key)
    }
    protected fun boolean(key: String? = null): SerializerDelegate<Boolean> {
        return serializer(Serializers.BOOLEAN, key)
    }
}