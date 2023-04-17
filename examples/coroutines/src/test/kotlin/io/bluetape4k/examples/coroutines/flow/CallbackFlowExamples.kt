package io.bluetape4k.examples.coroutines.flow

import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.info
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class CallbackFlowExamples {

    companion object: KLogging()

    /**
     * TODO: Kafka Producer의 Callback 을 `callbackFlow` 를 이용하여 Flow 로 구현하는 예제를 만들자
     */

    data class Message(val id: Long, val body: String)
    data class Result(val id: Long)

    interface ProduceApi {
        suspend fun produce(message: Message, callback: suspend (Result) -> Unit)
    }

    private class FakeProductApi: ProduceApi {
        override suspend fun produce(message: Message, callback: suspend (Result) -> Unit) {
            delay(1000)
            val result = Result(message.id)
            callback(result)
        }
    }

    // Kafka Producing 에 쓸 수 있다
    private fun flowFrom(api: ProduceApi, message: Flow<Message>): Flow<Result> = callbackFlow {
        val callback = { result: Result ->
            log.debug { "produce: $result" }
            trySend(result)
            Unit
        }
        message
            .onEach { message -> api.produce(message, callback) }
            .onCompletion { channel.close() }
            .collect()
    }

    @Test
    fun `get messages by callback flow`() = runSuspendTest {
        val api = FakeProductApi()

        val messages = flowOf(
            Message(1, "Message 1"),
            Message(2, "Message 2"),
            Message(3, "Message 3"),
        )

        val results = flowFrom(api, messages)

        results
            .onEach { log.info { "Callback result: $it" } }
            .collect()

        results.toList().map { it.id } shouldBeEqualTo listOf(1, 2, 3)
    }
}
