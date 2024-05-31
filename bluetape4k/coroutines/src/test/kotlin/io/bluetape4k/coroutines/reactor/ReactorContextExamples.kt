package io.bluetape4k.coroutines.reactor


import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.util.context.Context

/**
 * Examples of Reactive context integration with Kotlin Coroutines.
 *
 * 참고: [Reactor Context](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-reactor/kotlinx.coroutines.reactor/-reactor-context/)
 */
class ReactorContextExamples {

    companion object: KLogging()

    private val key = "answer"
    private val value = "42"

    @Test
    fun `coroutines에서 전달하는 context를 Reactor에서 사용하기`() = runTest {

        var captured: String? = null
        val flux = Flux.just("A")
            .contextWrite { context ->
                captured = context.getOrNull(key)
                context
            }
        flux.awaitFirst()
        captured.shouldBeNull()

        // 이제 ReactorContext에 key-value를 전달합니다.
        withContext(Context.of(key, value).asCoroutineContext()) {
            flux.awaitFirst()
        }
        captured shouldBeEqualTo value
    }

    @Test
    fun `reactor의 context를 coroutines에서 사용하기`() = runTest {
        var captured: String? = null

        val flow = flow {
            // captured = currentCoroutineContext()[ReactorContext]?.context?.getOrNull(key)
            captured = currentReactiveContext()?.getOrNull(key)
            emit("A")
        }

        // ReactorContext에 아무 값도 전달되지 않았으므로, captured는 null입니다.
        flow.asFlux()
            .subscribe()

        captured.shouldBeNull()

        // contextWrite 에서 Context에 key-value를 저장하므로, flow 에서 이를 사용할 수 있습니다.
        flow.asFlux()
            .contextWrite { context -> context.put(key, value) }
            .subscribe()

        captured shouldBeEqualTo value
    }
}
