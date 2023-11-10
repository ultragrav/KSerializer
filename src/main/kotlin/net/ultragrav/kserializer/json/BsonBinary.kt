package net.ultragrav.kserializer.json

data class BsonBinary(val type: BsonBinaryType, val value: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BsonBinary

        if (type != other.type) return false
        return value.contentEquals(other.value)
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + value.contentHashCode()
        return result
    }
}

enum class BsonBinaryType(val id: Byte) {
    GENERIC(0x0),
    FUNCTION(0x1),
    OLD_BINARY(0x2),
    UUID_LEGACY(0x3),
    UUID(0x4),
    MD5(0x5),
    ENCRYPTED(0x6),
    COLUMN(0x7),
    USER_DEFINED(0x80.toByte());

    companion object {
        fun byId(byte: Byte): BsonBinaryType? {
            return entries.firstOrNull { it.id == byte }
        }
    }
}