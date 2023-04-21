package io.bluetape4k.collections.eclipse

import io.bluetape4k.collections.toMapEntry
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class TupleSupportTest {

    companion object: KLogging()

    @Test
    fun `pair to eclipse pair`() {
        val origin = (1 to "a")
        val tp = origin.toTuplePair()
        tp.one shouldBeEqualTo 1
        tp.two shouldBeEqualTo "a"

        val pair = tp.toPair()
        pair shouldBeEqualTo origin
    }

    @Test
    fun `convert pair with Map Entry`() {
        val origin = (1 to "a")
        origin.toMapEntry()
    }
}
