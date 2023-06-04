plugins {
    kotlin("jvm") version "1.8.21"
    `maven-publish`
}

group = "net.ultragrav"
version = "1.0.0"

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

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
        }
    }
}