package io.bluetape4k.examples.coroutines.flow

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
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
            delay(100)
            val result = Result(message.id)
            callback(result)
        }
    }

    // Kafka Producing 에 쓸 수 있다
    private fun flowFrom(api: ProduceApi, message: Flow<Message>): Flow<Result> = callbackFlow {
        val callback = { result: Result ->
            log.debug { "produced result: $result" }
            trySend(result)
            Unit
        }
        message
            .onEach { message -> api.produce(message, callback) }
            .onCompletion { channel.close() }
            .collect()
    }

    @Test
    fun `get messages by callback flow`() = runTest {
        val api = FakeProductApi()

        val messages = flowOf(
            Message(1, "Message 1"),
            Message(2, "Message 2"),
            Message(3, "Message 3"),
        )

        val results = flowFrom(api, messages)

        val resultIds = results
            .onEach { log.debug { "Callback result: $it" } }
            .toList()
            .map { it.id }

        resultIds shouldBeEqualTo listOf(1, 2, 3)
    }
}
