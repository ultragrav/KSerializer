package net.ultragrav.kserializer.json

interface IJsonObject : JsonIndexable<String> {
    fun createObject(): IJsonObject
}