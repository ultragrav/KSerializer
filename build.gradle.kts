plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    `maven-publish`
}

group = "net.ultragrav"
version = "1.1.9"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://mvn.ultradev.app/snapshots")
}

dependencies {
    api("net.ultragrav", "Serializer", "1.2.23")
    api("org.jetbrains.kotlinx", "kotlinx-serialization-core", "1.6.0")
    api("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.6.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        repositories {
            maven {
                name = "ultradevRepository"
                url = uri("https://mvn.ultradev.app/snapshots")
                credentials(PasswordCredentials::class)
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
        }
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
        }
    }
}