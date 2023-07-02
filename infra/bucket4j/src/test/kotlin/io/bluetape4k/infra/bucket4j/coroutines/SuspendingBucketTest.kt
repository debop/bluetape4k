package io.bluetape4k.infra.bucket4j.coroutines

import io.bluetape4k.infra.bucket4j.AbstractBucket4jTest
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Refill
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class SuspendingBucketTest: AbstractBucket4jTest() {

    private lateinit var bucket: SuspendingBucket

    @BeforeEach
    fun beforeEach() {
        bucket = SuspendingBucket {
            addLimit(Bandwidth.classic(5, Refill.intervally(1, 1.seconds.toJavaDuration())))
        }
    }

    @Nested
    inner class TryConsume {

        @Test
        fun `should throw if tokensToConsume is equal to 0`() = runTest {
            assertFailsWith<IllegalArgumentException> {
                bucket.tryConsume(0L, 3L.seconds)
            }
        }

        @Test
        fun `should throw if tokensToConsume is less than 0`() = runTest {
            assertFailsWith<IllegalArgumentException> {
                bucket.tryConsume(-1L, 3L.seconds)
            }
        }

        @Test
        fun `should throw if maxWaitTime is a 0 length duration`() = runTest {
            assertFailsWith<IllegalArgumentException> {
                bucket.tryConsume(1L, 0L.seconds)
            }
        }

        @Test
        fun `should throw if maxWaitTime is a negative duration`() = runTest {
            assertFailsWith<IllegalArgumentException> {
                bucket.tryConsume(1L, (-3).seconds)
            }
        }

        @Test
        fun `impossible requests should return false immediatly`() = runTest {
            bucket.tryConsume(6L, 10.milliseconds).shouldBeFalse()
        }

        @Test
        fun `should delay for the required token filling time`() = runTest {
            val done = atomic(false)

            val defferedConsumed = async {
                val consumed = bucket.tryConsume(8L, 4.seconds)
                done.value = true
                consumed
            }

            // should need to wait 3 seconds to accumulate enough tokens, given 5 are covered by the starting
            // capacity, and we need 3 seconds to accumulate the 3 remaining
            done.value.shouldBeFalse()
            advanceTimeBy(2.5.seconds.inWholeMilliseconds)
            done.value.shouldBeFalse()
            advanceTimeBy(0.5.seconds.inWholeMilliseconds)

            done.value.shouldBeTrue()
            defferedConsumed.await().shouldBeTrue()
        }

    }

    @Nested
    inner class Consume {

        @Test
        fun `should throw if tokensToConsume is 0`() = runTest {
            assertFailsWith<IllegalArgumentException> {
                bucket.consume(0L)
            }
        }

        @Test
        fun `should throw if tokensToConsume is less than 0`() = runTest {
            assertFailsWith<IllegalArgumentException> {
                bucket.consume(-1L)
            }
        }

        @Test
        fun `should delay for the required token filling time`() = runTest {
            val done = atomic(false)

            launch {
                bucket.consume(9L)
                done.value = true
            }

            // should take 4 seconds to accumulate the required number of tokens
            done.value.shouldBeFalse()
            advanceTimeBy(3.seconds.inWholeMilliseconds)
            done.value.shouldBeFalse()

            advanceTimeBy(1.seconds.inWholeMilliseconds)

            await atMost 1.seconds.toJavaDuration() until { done.value }
            done.value.shouldBeTrue()
        }
    }
}
