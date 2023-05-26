package net.ultragrav.kserializer.serialization

import net.ultragrav.kserializer.json.JsonArray
import net.ultragrav.kserializer.json.JsonObject
import net.ultragrav.serializer.GravSerializer

object TinySerializer {
    fun write(data: Any?, serializer: GravSerializer) {
        when (data) {
            null -> serializer.writeByte(0)

            is JsonObject -> {
                serializer.writeByte(1)
                serializer.writeInt(data.backingMap.size)
                for ((key, value) in data.backingMap) {
                    serializer.writeString(key)
                    write(value, serializer)
                }
            }

            is JsonArray -> {
                serializer.writeByte(2)
                serializer.writeInt(data.backingList.size)
                for (value in data.backingList) {
                    write(value, serializer)
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
        }
    }

    fun read(serializer: GravSerializer): Any? {
        val type = serializer.readByte().toInt()
        when (type) {
            0 -> return null
            1 -> {
                val obj = JsonObject()
                val size = serializer.readInt()
                for (i in 0 until size) {
                    val key = serializer.readString()
                    val value = read(serializer) ?: continue
                    obj.backingMap[key] = value
                }
                return obj
            }

            2 -> {
                val array = JsonArray()
                val size = serializer.readInt()
                for (i in 0 until size) {
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
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}