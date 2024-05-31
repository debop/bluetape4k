package io.bluetape4k.collections.enhanced

import io.bluetape4k.collections.toList
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class CompositeCollectionTest {

    companion object: KLogging()

    @Test
    fun `create composite collection`() {
        val c1 = mutableListOf(1, 2, 3)
        val c2 = mutableListOf(4, 5, 6)

        val cc = CompositeCollection(c1, c2)

        cc.size shouldBeEqualTo 6
        cc.iterator().toList() shouldBeEqualTo listOf(1, 2, 3, 4, 5, 6)
    }
}
