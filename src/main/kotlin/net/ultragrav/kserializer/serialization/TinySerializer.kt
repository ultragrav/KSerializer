package net.ultragrav.kserializer.serialization

import net.ultragrav.kserializer.json.*
import net.ultragrav.serializer.GravSerializer

object TinySerializer {
    fun write(serializer: GravSerializer, data: Any?) {
        when (data) {
            null -> serializer.writeByte(0)

            is JsonObject -> {
                serializer.writeByte(1)
                serializer.writeInt(data.size)
                for (key in data.keys) {
                    serializer.writeString(key)
                    write(serializer, data[key])
                }
            }

            is JsonArray -> {
                serializer.writeByte(2)
                serializer.writeInt(data.size)
                for (key in data.keys) {
                    write(serializer, data[key])
                }
            }

            is String -> {
                serializer.writeByte(3)
                serializer.writeString(data)
            }

            is Double -> {
                serializer.writeByte(4)
                serializer.writeDouble(data.toDouble())
            }

            true -> serializer.writeByte(5)
            false -> serializer.writeByte(6)

            is BsonBinary -> {
                serializer.writeByte(8)
                serializer.writeByte(data.type.id)
                serializer.writeByteArray(data.value)
            }

            is Int -> {
                serializer.writeByte(9)
                serializer.writeInt(data)
            }

            is Long -> {
                serializer.writeByte(10)
                serializer.writeLong(data)
            }

            else -> throw IllegalArgumentException("Unknown type: ${data::class.java}")
        }
    }

    fun read(serializer: GravSerializer): Any? = readJson(serializer).value

    fun readJson(serializer: GravSerializer): JsonValue<*> {
        when (val type = serializer.readByte().toInt()) {
            0 -> return JsonValue(null, JsonType.NULL)
            1 -> {
                val size = serializer.readInt()
                val obj = JsonObject(size)
                for (i in 0..<size) {
                    val key = serializer.readString()
                    val value = readJson(serializer)
                    value.write(obj, key)
                }
                return JsonValue(obj, JsonType.OBJECT)
            }

            2 -> {
                val size = serializer.readInt()
                val array = JsonArray(size)
                for (i in 0..<size) {
                    val value = readJson(serializer)
                    value.add(array)
                }
                return JsonValue(array, JsonType.ARRAY)
            }

            3 -> return JsonValue(serializer.readString(), JsonType.STRING)
            4 -> return JsonValue(serializer.readDouble(), JsonType.NUMBER)

            5 -> return JsonValue(true, JsonType.BOOLEAN)
            6 -> return JsonValue(false, JsonType.BOOLEAN)
            7 -> {
                // Deprecated
                val size = serializer.readInt()
                return JsonValue(serializer.readBytes(size), JsonType.GENERIC_BINARY)
            }
            8 -> {
                val bsonType = BsonBinaryType.byId(serializer.readByte()) ?: throw IllegalArgumentException("Unknown binary type: $type")
                val value = serializer.readByteArray()
                return JsonValue(BsonBinary(bsonType, value), JsonType.BINARY)
            }
            9 -> return JsonValue(serializer.readInt(), JsonType.INT)
            10 -> return JsonValue(serializer.readLong(), JsonType.LONG)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}