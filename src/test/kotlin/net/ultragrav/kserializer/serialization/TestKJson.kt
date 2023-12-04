package net.ultragrav.kserializer.serialization

import net.ultragrav.kserializer.kotlinx.KJson
import java.util.*
import kotlin.test.Test

class TestKJson {
    @Test
    fun testPrimitive() {
        val obj = UUID.randomUUID()
        val json = KJson.encode(obj)
        val decoded = KJson.decode<UUID>(json)

        assert(obj == decoded)
    }
}