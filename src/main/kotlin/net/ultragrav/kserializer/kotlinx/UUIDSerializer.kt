package net.ultragrav.kserializer.kotlinx

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = buildClassSerialDescriptor("UUID")

    override fun deserialize(decoder: Decoder): UUID {
        if (decoder is JsonDecoder<*>) {
            return decoder.decodeUUID()
        }
        throw UnsupportedOperationException("Cannot deserialize uuid from non-JsonDecoder")
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        if (encoder is JsonEncoder<*>) {
            encoder.encodeUUID(value)
            return
        }
        throw UnsupportedOperationException("Cannot serialize uuid to non-JsonEncoder")
    }
}