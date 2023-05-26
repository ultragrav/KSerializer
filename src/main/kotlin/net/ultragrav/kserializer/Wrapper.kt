package net.ultragrav.kserializer

abstract class Wrapper(internal val data: JsonData) {


    protected fun <T> serializer(key: String, ser: JsonDataSerializer<T>): SerializerDelegate<T> {
        return SerializerDelegate(key, ser)
    }

    fun string(key: String): SerializerDelegate<String> {
        return SerializerDelegate(key, Serializers.STRING)
    }
}