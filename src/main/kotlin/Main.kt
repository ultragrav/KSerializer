import net.ultragrav.kserializer.JsonData
import net.ultragrav.kserializer.Wrapper

class TestWrapper(data: JsonData) : Wrapper(data) {
    var test by string("test")
    val wrapper by wrapper("wrapper", ::OtherWrapper)
}

class OtherWrapper(data: JsonData) : Wrapper(data) {
    var other by string("other")
}

fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")
}