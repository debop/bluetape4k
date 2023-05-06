package io.bluetape4k.concurrent

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.assertFailsWith

class ConcurrencyReducerTest {

    @Test
    fun `invalid max concurrency`() {
        // max concurrency 값은 양수이어야 합니다.
        assertFailsWith<IllegalArgumentException> {
            ConcurrencyReducer<Any>(0, 10)
        }
    }

    @Test
    fun `invalid max queue size`() {
        // max queue size 값은 양수이어야 합니다.
        assertFailsWith<IllegalArgumentException> {
            ConcurrencyReducer<Any>(10, 0)
        }
    }

    @Test
    fun `task return null`() {
        val limiter = ConcurrencyReducer<String>(1, 10)
        val promise = limiter.add(job(null))

        promise.isDone.shouldBeTrue()
        val exception = promise.getException()
        exception shouldBeInstanceOf NullPointerException::class
    }

    @Test
    fun `when job throw exception`() {
        val limiter = ConcurrencyReducer<String>(1, 10)
        val promise = limiter.add { throw IllegalStateException("Boom!") }

        promise.isDone.shouldBeTrue()
        promise.getException() shouldBeInstanceOf IllegalStateException::class
    }

    @Test
    fun `when job return failure`() {
        val limiter = ConcurrencyReducer<String>(1, 10)
        val promise = limiter.add {
            failedCompletableFutureOf(IllegalStateException("Boom!"))
        }

        promise.isDone.shouldBeTrue()
        promise.getException() shouldBeInstanceOf IllegalStateException::class
    }

    @Test
    fun `when job canceled`() {
        val limiter = ConcurrencyReducer<String>(2, 10)
        val request1 = CompletableFuture<String>()
        val request2 = CompletableFuture<String>()

        val promise1 = limiter.add(job(request1))
        val promise2 = limiter.add(job(request2))

        val wasInvoked = AtomicBoolean()
        val promise3 = limiter.add {
            wasInvoked.set(true)
            null
        }

        promise3.cancel(false)

        // 1 and 2 are in progress, 3 is cancelled
        promise1.isDone.shouldBeFalse()
        promise2.isDone.shouldBeFalse()
        promise3.isDone.shouldBeTrue()
        limiter.activeCount shouldBeEqualTo 2
        limiter.queuedCount shouldBeEqualTo 1

        request2.complete("2")

        promise1.isDone.shouldBeFalse()
        promise2.isDone.shouldBeTrue()
        promise3.isDone.shouldBeTrue()
        limiter.activeCount shouldBeEqualTo 1
        limiter.queuedCount shouldBeEqualTo 0

        request1.complete("1")

        promise1.isDone.shouldBeTrue()
        promise2.isDone.shouldBeTrue()
        promise3.isDone.shouldBeTrue()
        limiter.activeCount shouldBeEqualTo 0
        limiter.queuedCount shouldBeEqualTo 0

        wasInvoked.get().shouldBeFalse()
    }

    @Test
    fun `when simple result`() {
        val limiter = ConcurrencyReducer<String>(2, 10)
        val request1 = CompletableFuture<String>()
        val request2 = CompletableFuture<String>()
        val request3 = CompletableFuture<String>()

        val promise1 = limiter.add { request1 }
        val promise2 = limiter.add { request2 }
        val promise3 = limiter.add { request3 }

        request3.complete("3")

        // 1 and 2 are in progress, 3 is still blocked
        promise1.isDone.shouldBeFalse()
        promise2.isDone.shouldBeFalse()
        promise3.isDone.shouldBeFalse()
        limiter.activeCount shouldBeEqualTo 2
        limiter.queuedCount shouldBeEqualTo 1

        request2.complete("2")

        promise1.isDone.shouldBeFalse()
        promise2.isDone.shouldBeTrue()
        promise3.isDone.shouldBeTrue()
        limiter.activeCount shouldBeEqualTo 1      // request3 이 이미 완료된 놈이므로
        limiter.queuedCount shouldBeEqualTo 0

        request1.complete("1")

        promise1.isDone.shouldBeTrue()
        promise2.isDone.shouldBeTrue()
        promise3.isDone.shouldBeTrue()
        limiter.activeCount shouldBeEqualTo 0
        limiter.queuedCount shouldBeEqualTo 0
    }

    @Test
    fun `when task long running`() {
        val activeCount = atomic(0)
        val maxCount = atomic(0)
        val queueSize = 11
        val maxConcurrency = 10
        val limiter = ConcurrencyReducer<String>(maxConcurrency, queueSize)

        val jobs = arrayListOf<CountingJob>()
        val promises = arrayListOf<CompletableFuture<String>>()

        repeat(queueSize) {
            val job = CountingJob(limiter::activeCount, maxCount)
            jobs.add(job)
            promises += limiter.add(job)
        }

        jobs.forEachIndexed { index, job ->
            if (index % 2 == 0) {
                job.future.complete("success")
            } else {
                job.future.completeExceptionally(IllegalStateException("Boom!"))
            }
        }

        promises.all { it.isDone }.shouldBeTrue()
        activeCount.value shouldBeEqualTo 0
        limiter.activeCount shouldBeEqualTo 0
        limiter.queuedCount shouldBeEqualTo 0
        limiter.remainingActiveCapacity shouldBeEqualTo maxConcurrency
        limiter.remainingQueueCapacity shouldBeEqualTo queueSize
        maxCount.value shouldBeEqualTo maxConcurrency
    }

    @Test
    fun `when exceed queue size`() {
        val limiter = ConcurrencyReducer<String>(10, 10)
        repeat(20) {
            limiter.add { CompletableFuture() }
        }

        val promise = limiter.add { CompletableFuture() }
        promise.isDone.shouldBeTrue()
        promise.getException() shouldBeInstanceOf ConcurrencyReducer.CapacityReachedException::class
    }

    @Test
    fun `verify queue size`() {
        val future = CompletableFuture<String>()
        val limiter = ConcurrencyReducer<String>(10, 10)
        repeat(20) {
            limiter.add { future }
        }

        limiter.activeCount shouldBeEqualTo 10
        limiter.queuedCount shouldBeEqualTo 10
        limiter.remainingActiveCapacity shouldBeEqualTo 0
        limiter.remainingQueueCapacity shouldBeEqualTo 0

        future.complete("")

        limiter.activeCount shouldBeEqualTo 0
        limiter.queuedCount shouldBeEqualTo 0
        limiter.remainingActiveCapacity shouldBeEqualTo 10
        limiter.remainingQueueCapacity shouldBeEqualTo 10
    }

    private fun job(future: CompletionStage<String>?): () -> CompletionStage<String>? = { future }

    private class CountingJob(
        private val activeCount: () -> Int,
        private val maxCount: AtomicInt,
    ): () -> CompletionStage<String>? {

        companion object: KLogging()

        val future = CompletableFuture<String>()

        override fun invoke(): CompletionStage<String>? {
            val count = activeCount()
            log.debug { "Active count=$count, maxCount=${maxCount.value}" }
            if (count > maxCount.value) {
                maxCount.value = count
            }
            return future
        }
    }
}
