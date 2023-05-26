import net.ultragrav.kserializer.Serializers
import net.ultragrav.kserializer.json.JsonObject
import net.ultragrav.kserializer.Wrapper
import net.ultragrav.kserializer.delegates.ListDelegate

class TestWrapper(data: JsonObject) : Wrapper(data) {
    var test by string()
    val wrapper by wrapper(::OtherWrapper)
}

class OtherWrapper(data: JsonObject) : Wrapper(data) {
    var other by string()
    val list by list(Serializers.STRING)
}

fun main() {
    val test = TestWrapper(JsonObject())

    test.test = "Hello"
    test.wrapper.other = "World"

    println("${test.test} ${test.wrapper.other}")


    test.wrapper.list.add("Hello")

    println(test.wrapper.list[0])
}