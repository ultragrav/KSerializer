package net.ultragrav.kserializer.serialization

import net.ultragrav.kserializer.json.JsonArray
import net.ultragrav.kserializer.json.JsonIndexable

interface JsonDataSerializer<T> {
    fun <K> serialize(data: JsonIndexable<K>, key: K, value: T): Any?
    fun <K> deserialize(data: JsonIndexable<K>, key: K): T

    fun serializeAdd(data: JsonArray, value: T, index: Int = -1)
}