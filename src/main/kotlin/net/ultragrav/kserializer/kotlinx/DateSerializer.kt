package net.ultragrav.kserializer.kotlinx

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

object DateSerializer : KSerializer<Date> {
    override val descriptor = buildClassSerialDescriptor("Date")

    override fun deserialize(decoder: Decoder): Date {
        if (decoder is JsonDecoder<*>) {
            return decoder.decodeDate()
        }
        throw UnsupportedOperationException("Cannot deserialize date from non-JsonDecoder")
    }

    override fun serialize(encoder: Encoder, value: Date) {
        if (encoder is JsonEncoder<*>) {
            encoder.encodeDate(value)
            return
        }
        throw UnsupportedOperationException("Cannot serialize date to non-JsonEncoder")
    }
}