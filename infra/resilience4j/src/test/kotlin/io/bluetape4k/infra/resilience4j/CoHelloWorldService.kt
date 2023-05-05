package io.bluetape4k.infra.resilience4j

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay


class CoHelloWorldService {

    private val invocationCounter = atomic(0)
    val invocationCount: Int by invocationCounter

    private val sync = Channel<Unit>(Channel.UNLIMITED)

    suspend fun returnHelloWorld(): String {
        delay(1) // so tests are fast, but compiler agrees suspend modifier is required
        invocationCounter.incrementAndGet()
        return "Hello world"
    }

    suspend fun returnMessage(message: String): String {
        delay(1) // so tests are fast, but compiler agrees suspend modifier is required
        invocationCounter.incrementAndGet()
        return message
    }

    suspend fun throwException() {
        delay(1) // so tests are fast, but compiler agrees suspend modifier is required
        invocationCounter.incrementAndGet()
        error("test exception")
    }

    suspend fun throwExceptionWithMessage(message: String): String {
        delay(1)
        invocationCounter.incrementAndGet()
        error(message)
    }

    /**
     * Suspend until a matching [proceed] call.
     */
    suspend fun wait() {
        invocationCounter.incrementAndGet()
        sync.receive()
    }

    /**
     * Allow a call into [wait] to proceed.
     */
    fun proceed(): Boolean = sync.trySend(Unit).isSuccess
}
