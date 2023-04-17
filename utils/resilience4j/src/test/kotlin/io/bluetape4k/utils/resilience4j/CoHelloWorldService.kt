package io.bluetape4k.utils.resilience4j

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay


class CoHelloWorldService {

    @Volatile
    var invocationCounter = 0
        private set

    private val sync = Channel<Unit>(Channel.UNLIMITED)

    suspend fun returnHelloWorld(): String {
        delay(0) // so tests are fast, but compiler agrees suspend modifier is required
        invocationCounter++
        return "Hello world"
    }

    suspend fun returnMessage(message: String): String {
        delay(0) // so tests are fast, but compiler agrees suspend modifier is required
        invocationCounter++
        return message
    }

    suspend fun throwException() {
        delay(0) // so tests are fast, but compiler agrees suspend modifier is required
        invocationCounter++
        error("test exception")
    }

    suspend fun throwExceptionWithMessage(message: String): String {
        delay(0)
        invocationCounter++
        error(message)
    }

    /**
     * Suspend until a matching [proceed] call.
     */
    suspend fun wait() {
        invocationCounter++
        sync.receive()
    }

    /**
     * Allow a call into [wait] to proceed.
     */
    fun proceed(): Boolean = sync.trySend(Unit).isSuccess
}
