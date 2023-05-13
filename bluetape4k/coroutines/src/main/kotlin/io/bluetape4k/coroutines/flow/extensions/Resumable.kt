package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.logging.KLogging
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Primitive for suspending and resuming coroutines on demand
 */
open class Resumable {

    companion object: KLogging() {
        private val READLY = ReadyContinuation()
        val VALUE = Any()

        val RESULT_SUCCESS = Result.success(VALUE)
    }

    private val continuationRef = atomic<Continuation<Any>?>(null)
    private val continuation by continuationRef

    suspend fun await() {
        suspendCancellableCoroutine<Any> { cont ->
            while (true) {
                val current = continuation
                if (current == READLY) {
                    cont.resumeWith(RESULT_SUCCESS)
                    break
                }
                if (current != null) {
                    throw IllegalStateException("Only one thread can await a Resumable")
                }
                if (continuationRef.compareAndSet(current, cont)) {
                    break
                }
            }
        }
        continuationRef.getAndSet(null)
    }

    fun resume() {
        if (continuation == READLY) {
            return
        }
        continuationRef.getAndSet(READLY)?.resumeWith(RESULT_SUCCESS)
    }

    /**
     * Represents a stateless indicator if the continuation is already
     * ready for resumption, thus no need to get suspended.
     */
    private class ReadyContinuation: Continuation<Any> {
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resumeWith(result: Result<Any>) {
            // The existence already indicates resumption
        }
    }
}
