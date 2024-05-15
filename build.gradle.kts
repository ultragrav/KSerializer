plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    `maven-publish`
}

group = "net.ultragrav"
version = "1.1.5"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://mvn.ultragrav.net/repository/maven-public/")
}

dependencies {
    api("net.ultragrav", "Serializer", "1.2.20")

    api("org.jetbrains.kotlinx", "kotlinx-serialization-core", "1.6.0")
//    api("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.6.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
        }
    }
}