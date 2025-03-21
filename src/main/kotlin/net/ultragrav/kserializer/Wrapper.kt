package net.ultragrav.kserializer

import net.ultragrav.kserializer.delegates.ListDelegate
import net.ultragrav.kserializer.delegates.SerializerDelegate
import net.ultragrav.kserializer.delegates.WrapperDelegate
import net.ultragrav.kserializer.delegates.WrapperListDelegate
import net.ultragrav.kserializer.json.JsonArray
import net.ultragrav.kserializer.json.JsonObject
import net.ultragrav.kserializer.json.JsonType
import net.ultragrav.kserializer.serialization.JsonDataSerializer
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

abstract class Wrapper(val data: JsonObject) {
    protected fun <T : Any> serializer(
        ser: JsonDataSerializer<T>,
        initial: T,
        key: String? = null
    ): SerializerDelegate<T> {
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

    protected inline fun <reified T : Enum<T>> enum(
        initial: T,
        key: String? = null
    ): SerializerDelegate<T> {
        return serializer(Serializers.enum(T::class.java), initial, key)
    }

    protected fun jsonObject(
        initial: JsonObject = JsonObject(),
        key: String? = null
    ): SerializerDelegate<JsonObject> {
        return serializer(JsonType.OBJECT, initial, key)
    }

    protected fun jsonArray(
        initial: JsonArray = JsonArray(),
        key: String? = null
    ): SerializerDelegate<JsonArray> {
        return serializer(JsonType.ARRAY, initial, key)
    }

    protected fun string(
        initial: String,
        key: String? = null
    ): SerializerDelegate<String> {
        return serializer(JsonType.STRING, initial, key)
    }

    protected fun int(
        initial: Int,
        key: String? = null
    ): SerializerDelegate<Int> {
        return serializer(Serializers.INT, initial, key)
    }

    protected fun long(
        initial: Long,
        key: String? = null
    ): SerializerDelegate<Long> {
        return serializer(Serializers.LONG, initial, key)
    }

    protected fun double(
        initial: Double,
        key: String? = null
    ): SerializerDelegate<Double> {
        return serializer(Serializers.DOUBLE, initial, key)
    }

    protected fun float(
        initial: Float,
        key: String? = null
    ): SerializerDelegate<Float> {
        return serializer(Serializers.FLOAT, initial, key)
    }

    protected fun boolean(
        initial: Boolean,
        key: String? = null
    ): SerializerDelegate<Boolean> {
        return serializer(JsonType.BOOLEAN, initial, key)
    }

    protected fun byteArray(
        initial: ByteArray,
        key: String? = null
    ): SerializerDelegate<ByteArray> {
        return serializer(JsonType.GENERIC_BINARY, initial, key)
    }

    protected fun bigInteger(
        initial: BigInteger,
        key: String? = null
    ): SerializerDelegate<BigInteger> {
        return serializer(Serializers.BIG_INTEGER, initial, key)
    }

    protected fun bigDecimal(
        initial: BigDecimal,
        key: String? = null
    ): SerializerDelegate<BigDecimal> {
        return serializer(Serializers.BIG_DECIMAL, initial, key)
    }

    protected fun date(
        initial: Date,
        key: String? = null
    ): SerializerDelegate<Date> {
        return serializer(JsonType.DATE, initial, key)
    }

    protected fun uuid(
        initial: UUID,
        key: String? = null
    ): SerializerDelegate<UUID> {
        return serializer(JsonType.UUID, initial, key)
    }
}