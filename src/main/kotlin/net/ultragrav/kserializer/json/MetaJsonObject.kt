package net.ultragrav.kserializer.json

import net.ultragrav.serializer.JsonMeta

class MetaJsonObject(val meta: JsonMeta) : IJsonObject {
    override fun getString(key: String): String {
        TODO("Not yet implemented")
    }

    override fun setString(key: String, value: String): Any? {
        TODO("Not yet implemented")
    }

    override fun getObject(key: String): IJsonObject {
        val obj = meta.get<JsonMeta>(key)
        return MetaJsonObject(obj)
    }

    override fun setObject(key: String, data: IJsonObject): Any? {
        if (data !is MetaJsonObject) throw IllegalArgumentException("Data must be a MetaJsonObject")
        val prev = meta.get<Any?>(key)
        meta.set(key, data.meta)
        return prev
    }

    override fun getArray(key: String): JsonArray {
        val list = meta.get<MutableList<Any?>>(key)
        val array = JsonArray()
        array.backingList = list
        return array
    }

    override fun setArray(key: String, array: JsonArray): Any? {
        val prev = meta.get<Any?>(key)
        meta.set(key, array.backingList)
        return prev
    }

    override fun getNumber(key: String): Number = meta[key]

    override fun setNumber(key: String, number: Number): Any? {
        val prev = meta.get<Any?>(key)
        meta.set(key, number)
        return prev
    }

    override fun getBoolean(key: String): Boolean = meta[key]

    override fun setBoolean(key: String, boolean: Boolean): Any? {
        val prev = meta.get<Any?>(key)
        meta.set(key, boolean)
        return prev
    }

    override fun getByteArray(key: String): ByteArray = meta[key]

    override fun setByteArray(key: String, byteArray: ByteArray): Any? {
        val prev = meta.get<Any?>(key)
        meta.set(key, byteArray)
        return prev
    }

    override fun remove(key: String): Any? {
        val ret = meta.get<Any?>(key)
        meta.remove(key)
        return ret
    }

    override fun clear() {
        meta.keys.toList().forEach { meta.remove(it) }
    }

    override fun contains(key: String): Boolean {
        return meta.keys.contains(key)
    }

    override val size get() = meta.keys.size

}