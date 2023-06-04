package net.ultragrav.kserializer.updates

import net.ultragrav.kserializer.json.JsonArray

class ArrayUpdates(array: JsonArray) : UpdateTracker<JsonArray> {
    private val updates = array.updateTracker!!.updates

    override fun apply(indexable: JsonArray) {
        updates.forEach { it.apply(indexable) }
    }
}