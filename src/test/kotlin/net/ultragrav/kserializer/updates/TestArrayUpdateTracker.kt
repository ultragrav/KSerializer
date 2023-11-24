package net.ultragrav.kserializer.updates

import net.ultragrav.kserializer.json.JsonArray
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.test.assertContentEquals

class TestArrayUpdateTracker {
    @Test
    fun test() {
        val tracker = ArrayUpdateTracker()

        val seed = ThreadLocalRandom.current().nextLong()
        val random = Random(seed)

        // Create two identical arrays
        val list = JsonArray()
        val originalList = JsonArray()
        for (i in 0..50) {
            val value = random.nextInt(100)
            list.addInt(value)
            originalList.addInt(value)
        }

        // Apply updates to one while tracking them in the tracker
        for (i in 0..1000) {
            // Randomly add, set, or remove
            val rand = random.nextDouble()

            if (list.size == 0 || rand < 0.33) {
                // Add
                val value = random.nextInt(100)
                val index = random.nextInt(list.size + 1)
                list.addInt(value, index)
                tracker.update(ArrayUpdateTracker.AddUpdate(index, value))
            } else if (rand < 0.66) {
                // Set
                val value = random.nextInt(100)
                val index = random.nextInt(list.size)
                list.setInt(index, value)
                tracker.update(ArrayUpdateTracker.SetUpdate(index, value))
            } else {
                // Remove
                val index = random.nextInt(list.size)
                list.remove(index)
                tracker.update(ArrayUpdateTracker.RemoveUpdate(index))
            }
        }

        // Apply the updates to the original list
        tracker.apply(originalList)

        // Compare the two lists
        assertContentEquals(list.backingList, originalList.backingList, "Lists differ")
    }
}