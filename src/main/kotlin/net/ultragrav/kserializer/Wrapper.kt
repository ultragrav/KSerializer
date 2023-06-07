package net.ultragrav.kserializer

import net.ultragrav.kserializer.delegates.ListDelegate
import net.ultragrav.kserializer.delegates.SerializerDelegate
import net.ultragrav.kserializer.delegates.WrapperDelegate
import net.ultragrav.kserializer.delegates.WrapperListDelegate
import net.ultragrav.kserializer.json.JsonArray
import net.ultragrav.kserializer.json.JsonObject
import net.ultragrav.kserializer.serialization.JsonDataSerializer
import java.math.BigDecimal
import java.math.BigInteger

abstract class Wrapper(val data: JsonObject) {
    protected fun <T : Any> serializer(ser: JsonDataSerializer<T>, key: String? = null, initial: T): SerializerDelegate<T> {
        return SerializerDelegate(ser, key, initial)
    }

    protected fun <T : Wrapper> wrapper(wrapperFactory: (JsonObject) -> T, key: String? = null): WrapperDelegate<T> {
        return WrapperDelegate(wrapperFactory, key)
    }

    protected fun <T> list(ser: JsonDataSerializer<T>, key: String? = null): ListDelegate<T> {
        return ListDelegate(ser, key)
    }

    protected fun <T : Wrapper> list(wrapperFactory: (JsonObject) -> T, key: String? = null): WrapperListDelegate<T> {
        return WrapperListDelegate(wrapperFactory, key)
    }

    protected inline fun <reified T : Enum<T>> enum(key: String? = null, initial: T): SerializerDelegate<T> {
        return serializer(Serializers.enum(T::class.java), key, initial)
    }

    protected fun jsonObject(
        key: String? = null,
        initial: JsonObject = JsonObject()
    ): SerializerDelegate<JsonObject> {
        return serializer(Serializers.JSON_OBJECT, key, initial = initial)
    }

    protected fun jsonArray(key: String? = null, initial: JsonArray = JsonArray()): SerializerDelegate<JsonArray> {
        return serializer(Serializers.JSON_ARRAY, key, initial = initial)
    }

    protected fun string(key: String? = null, initial: String): SerializerDelegate<String> {
        return serializer(Serializers.STRING, key, initial)
    }

    protected fun int(key: String? = null, initial: Int): SerializerDelegate<Int> {
        return serializer(Serializers.INT, key, initial)
    }

    protected fun long(key: String? = null, initial: Long): SerializerDelegate<Long> {
        return serializer(Serializers.LONG, key, initial)
    }

    protected fun double(key: String? = null, initial: Double): SerializerDelegate<Double> {
        return serializer(Serializers.DOUBLE, key, initial)
    }

    protected fun float(key: String? = null, initial: Float): SerializerDelegate<Float> {
        return serializer(Serializers.FLOAT, key, initial)
    }

    protected fun boolean(key: String? = null, initial: Boolean): SerializerDelegate<Boolean> {
        return serializer(Serializers.BOOLEAN, key, initial)
    }

    protected fun byteArray(key: String? = null, initial: ByteArray): SerializerDelegate<ByteArray> {
        return serializer(Serializers.BYTE_ARRAY, key, initial)
    }

    protected fun bigInteger(key: String? = null, initial: BigInteger): SerializerDelegate<BigInteger> {
        return serializer(Serializers.BIG_INTEGER, key, initial)
    }

    protected fun bigDecimal(key: String? = null, initial: BigDecimal): SerializerDelegate<BigDecimal> {
        return serializer(Serializers.BIG_DECIMAL, key, initial)
    }
}