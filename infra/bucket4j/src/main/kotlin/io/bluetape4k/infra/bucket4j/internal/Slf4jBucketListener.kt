package io.bluetape4k.infra.bucket4j.internal

import io.bluetape4k.logging.KotlinLogging
import io.bluetape4k.logging.debug
import io.github.bucket4j.BucketListener
import kotlinx.atomicfu.atomic

class Slf4jBucketListener(val log: org.slf4j.Logger = KotlinLogging.logger {}): BucketListener {

    private val consumedCounter = atomic(0L)
    private val rejectedCounter = atomic(0L)
    private val delayedNanosCounter = atomic(0L)
    private val parkedNanosCounter = atomic(0L)
    private val interruptedCounter = atomic(0L)

    val consumed by consumedCounter
    val rejected by rejectedCounter
    val delayedNanos by delayedNanosCounter
    val parkedNanos by parkedNanosCounter
    val interrupted by interruptedCounter

    override fun onConsumed(tokens: Long) {
        consumedCounter.addAndGet(tokens)
        log.debug { "Bucket on consumed($tokens). all consumed=$consumed" }
    }

    override fun onRejected(tokens: Long) {
        rejectedCounter.addAndGet(tokens)
        log.debug { "Bucket on rejected($tokens). all rejected=$rejected" }
    }

    override fun onDelayed(nanos: Long) {
        delayedNanosCounter.addAndGet(nanos)
        log.debug { "Bucket on delayed($nanos). all delayed nanos=$delayedNanos" }
    }

    override fun onParked(nanos: Long) {
        parkedNanosCounter.addAndGet(nanos)
        log.debug { "Bucket on parked($nanos). all parked nanos=$parkedNanos" }
    }

    override fun onInterrupted(e: InterruptedException?) {
        interruptedCounter.incrementAndGet()
        log.debug(e) { "Bucket on interrupted. all interrupted=$interrupted" }
    }
}
