package net.ultragrav.kserializer.serialization

import net.ultragrav.kserializer.json.BsonBinary
import net.ultragrav.kserializer.json.BsonBinaryType
import net.ultragrav.kserializer.json.JsonArray
import net.ultragrav.kserializer.json.JsonObject
import net.ultragrav.serializer.GravSerializer

object TinySerializer {
    fun write(serializer: GravSerializer, data: Any?) {
        when (data) {
            null -> serializer.writeByte(0)

            is JsonObject -> {
                serializer.writeByte(1)
                serializer.writeInt(data.backingMap.size)
                for ((key, value) in data.backingMap) {
                    serializer.writeString(key)
                    write(serializer, value)
                }
            }

            is JsonArray -> {
                serializer.writeByte(2)
                serializer.writeInt(data.backingList.size)
                for (value in data.backingList) {
                    write(serializer, value)
                }
            }

            is String -> {
                serializer.writeByte(3)
                serializer.writeString(data)
            }

            is Number -> {
                serializer.writeByte(4)
                // TODO: don't always use double?
                serializer.writeDouble(data.toDouble())
            }

            true -> serializer.writeByte(5)
            false -> serializer.writeByte(6)

            is ByteArray -> {
                serializer.writeByte(7)
                serializer.writeInt(data.size)
                serializer.append(data)
            }

            is BsonBinary -> {
                serializer.writeByte(8)
                serializer.writeByte(data.type.id)
                serializer.writeByteArray(data.value)
            }

            else -> throw IllegalArgumentException("Unknown type: ${data::class.java}")
        }
    }

    fun read(serializer: GravSerializer): Any? {
        when (val type = serializer.readByte().toInt()) {
            0 -> return null
            1 -> {
                val size = serializer.readInt()
                val obj = JsonObject(size)
                for (i in 0..<size) {
                    val key = serializer.readString()
                    val value = read(serializer) ?: continue
                    obj.backingMap[key] = value
                }
                return obj
            }

            2 -> {
                val size = serializer.readInt()
                val array = JsonArray(size)
                for (i in 0..<size) {
                    val value = read(serializer) ?: continue
                    array.backingList.add(value)
                }
                return array
            }

            3 -> {
                return serializer.readString()
            }

            4 -> {
                return serializer.readDouble()
            }

            5 -> return true
            6 -> return false
            7 -> {
                val size = serializer.readInt()
                return serializer.readBytes(size)
            }
            8 -> {
                val bsonType = BsonBinaryType.byId(serializer.readByte()) ?: throw IllegalArgumentException("Unknown binary type: $type")
                val value = serializer.readByteArray()
                return BsonBinary(bsonType, value)
            }
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}