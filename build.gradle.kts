plugins {
    kotlin("jvm") version "1.8.21"
    application
}

group = "net.ultragrav"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://mvn.ultragrav.net/")
}

dependencies {
    api("net.ultragrav", "Serializer", "1.2.15")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}