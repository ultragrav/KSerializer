package net.ultragrav.kserializer

interface JsonDataSerializer<T> {
    fun serialize(data: JsonData, key: String, value: T)
    fun deserialize(data: JsonData, key: String): T
}