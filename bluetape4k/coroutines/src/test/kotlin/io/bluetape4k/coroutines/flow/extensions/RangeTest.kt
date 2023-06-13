package io.bluetape4k.coroutines.flow.extensions

import app.cash.turbine.test
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class RangeTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `empty range`() = runTest {
        range(0, 0).test { awaitComplete() }
        range(0, -1).test { awaitComplete() }

        range(0L, 0).test { awaitComplete() }
        range(0L, -1).test { awaitComplete() }
    }

    @Test
    fun `range of int`() = runTest {
        rangeInt(0, 3).log("rangeInt")
            .test {
                awaitItem() shouldBeEqualTo 0
                awaitItem() shouldBeEqualTo 1
                awaitItem() shouldBeEqualTo 2
                awaitComplete()
            }
    }

    @Test
    fun `range of long`() = runTest {
        rangeLong(0L, 3).log("rangeLong")
            .test {
                awaitItem() shouldBeEqualTo 0L
                awaitItem() shouldBeEqualTo 1L
                awaitItem() shouldBeEqualTo 2L
                awaitComplete()
            }
    }
}
