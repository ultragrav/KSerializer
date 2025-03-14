package net.ultragrav.kserializer.util

import java.util.*

fun UUID.toBytes(): ByteArray {
    val binary = ByteArray(16)
    val msb = this.mostSignificantBits
    val lsb = this.leastSignificantBits
    binary[0] = ((msb shr 56) and 0xFF).toByte()
    binary[1] = ((msb shr 48) and 0xFF).toByte()
    binary[2] = ((msb shr 40) and 0xFF).toByte()
    binary[3] = ((msb shr 32) and 0xFF).toByte()
    binary[4] = ((msb shr 24) and 0xFF).toByte()
    binary[5] = ((msb shr 16) and 0xFF).toByte()
    binary[6] = ((msb shr 8) and 0xFF).toByte()
    binary[7] = (msb and 0xFF).toByte()
    binary[8] = ((lsb shr 56) and 0xFF).toByte()
    binary[9] = ((lsb shr 48) and 0xFF).toByte()
    binary[10] = ((lsb shr 40) and 0xFF).toByte()
    binary[11] = ((lsb shr 32) and 0xFF).toByte()
    binary[12] = ((lsb shr 24) and 0xFF).toByte()
    binary[13] = ((lsb shr 16) and 0xFF).toByte()
    binary[14] = ((lsb shr 8) and 0xFF).toByte()
    binary[15] = (lsb and 0xFF).toByte()
    return binary
}

fun ByteArray.toUUID(): UUID {
    val msb = (this[0].toUByte().toLong() shl 56) or
            (this[1].toUByte().toLong() shl 48) or
            (this[2].toUByte().toLong() shl 40) or
            (this[3].toUByte().toLong() shl 32) or
            (this[4].toUByte().toLong() shl 24) or
            (this[5].toUByte().toLong() shl 16) or
            (this[6].toUByte().toLong() shl 8) or
            this[7].toUByte().toLong()
    val lsb = (this[8].toUByte().toLong() shl 56) or
            (this[9].toUByte().toLong() shl 48) or
            (this[10].toUByte().toLong() shl 40) or
            (this[11].toUByte().toLong() shl 32) or
            (this[12].toUByte().toLong() shl 24) or
            (this[13].toUByte().toLong() shl 16) or
            (this[14].toUByte().toLong() shl 8) or
            this[15].toUByte().toLong()
    return UUID(msb, lsb)
}
