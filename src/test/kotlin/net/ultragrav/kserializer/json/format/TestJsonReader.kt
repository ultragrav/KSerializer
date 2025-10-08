package net.ultragrav.kserializer.json.format

import net.ultragrav.kserializer.json.JsonArray
import kotlin.test.Test

class TestJsonReader {
    @Test
    fun testMinifiedArray() {
        val input = "[1.1,2.2,3.3]"
        val reader = JsonReader(input.reader())
        val result = reader.read()
        assert(result is JsonArray)
        val array = result as JsonArray
        assert(array.get<Double>(0) == 1.1)
        assert(array.get<Double>(1) == 2.2)
        assert(array.get<Double>(2) == 3.3)
    }
}