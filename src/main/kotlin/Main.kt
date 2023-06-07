import net.ultragrav.kserializer.Serializers
import net.ultragrav.kserializer.Wrapper
import net.ultragrav.kserializer.json.JsonObject

class TestWrapper(data: JsonObject) : Wrapper(data) {
    var test by string(initial = "Hello")
    var enum by enum<TestEnum>(initial = TestEnum.TEST1)
    val wrapper by wrapper(::OtherWrapper)
    val wrapperList by list(::OtherWrapper)
}

class OtherWrapper(data: JsonObject) : Wrapper(data) {
    var other by string().cache()
    val list by list(Serializers.STRING)
}

enum class TestEnum {
    TEST1
}

fun main() {
    val testList = mutableListOf<String>()
    testList.add(0, "Hello")
    // Did you throw an exception?

    val test = TestWrapper(JsonObject())

    test.test = "Hello"
    test.wrapper.other = "World"
    test.enum = TestEnum.TEST1
    test.wrapper.list.add("Hello")
    test.wrapperList.add(OtherWrapper(JsonObject()).apply { other = "World" })

    println(test.data)
}