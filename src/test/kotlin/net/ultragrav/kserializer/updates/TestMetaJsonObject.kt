package net.ultragrav.kserializer.updates

import net.ultragrav.kserializer.Wrapper
import net.ultragrav.kserializer.json.IJsonObject
import net.ultragrav.kserializer.json.MetaJsonObject
import net.ultragrav.serializer.JsonMeta
import org.junit.jupiter.api.Test

class TestMetaJsonObject {

    private class TestWrapper(data: IJsonObject) : Wrapper(data) {
        var num by int(0)
        val nested by wrapper(::NestedWrapper)
    }

    private class NestedWrapper(data: IJsonObject) : Wrapper(data) {
        var num by int(0)
    }

    @Test
    fun testRecord() {
        val meta = JsonMeta(true)
        val obj = MetaJsonObject(meta)
        val wrapper = TestWrapper(obj)
        wrapper.nested.num = 5
        wrapper.num = 3

        assert(meta.get<Int>("num") == 3)
        assert(meta.get<JsonMeta>("nested") is JsonMeta)
        assert(meta.get<JsonMeta>("nested").get<Int>("num") == 5)

        val reduced = meta.reduce()

        assert(reduced.get<Int>("num") == 3)
        assert(reduced.get<JsonMeta>("nested") is JsonMeta)
        assert(reduced.get<JsonMeta>("nested").get<Int>("num") == 5)
    }
}