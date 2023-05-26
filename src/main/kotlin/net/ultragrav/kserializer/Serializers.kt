package net.ultragrav.kserializer

object Serializers {
    val STRING = object : JsonDataSerializer<String> {
        override fun serialize(data: JsonData, key: String, value: String) {
            // Does nothing for now
        }

        override fun deserialize(data: JsonData, key: String): String {
            return "test string"
        }
    }

    fun string(key: String): SerializerDelegate<String> {
        return SerializerDelegate(key, STRING)
    }
}