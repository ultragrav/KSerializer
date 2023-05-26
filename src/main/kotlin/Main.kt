import net.ultragrav.kserializer.Serializers
import net.ultragrav.kserializer.Wrapper
import net.ultragrav.kserializer.json.JsonObject

class TestWrapper(data: JsonObject) : Wrapper(data) {
    var test by string()
    var enum by enum(TestEnum::class)
    val wrapper by wrapper(::OtherWrapper)
}

class OtherWrapper(data: JsonObject) : Wrapper(data) {
    var other by string().cache()
    val list by list(Serializers.STRING)
}

enum class TestEnum {
    TEST1
}

fun main() {
    val test = TestWrapper(JsonObject())

    test.test = "Hello"
    test.wrapper.other = "World"
    test.enum = TestEnum.TEST1

    println("${test.test} ${test.wrapper.other}")
    println(test.wrapper.other)


    test.wrapper.list.add("Hello")

    println(test.wrapper.list[0])

    println(test.data)
}