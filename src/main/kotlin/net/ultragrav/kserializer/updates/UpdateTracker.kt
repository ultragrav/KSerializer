package net.ultragrav.kserializer.updates

import net.ultragrav.kserializer.json.JsonIndexable

interface UpdateTracker<T : JsonIndexable<*>> {
    fun apply(indexable: T)

    @Suppress("UNCHECKED_CAST")
    fun unsafeApply(indexable: Any) {
        apply(indexable as T)
    }

    interface Update<T : JsonIndexable<*>> {
        fun apply(indexable: T)
    }
}