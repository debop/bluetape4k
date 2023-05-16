package io.bluetape4k.workshop.resilience4j.service.coroutines

import io.bluetape4k.logging.KLogging
import io.github.resilience4j.bulkhead.annotation.Bulkhead
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.time.delay
import org.springframework.stereotype.Component
import java.io.IOException
import java.time.Duration

@Component(value = "backendBCoroutineService")
class BackendBCoroutineService: CoroutineService {

    companion object: KLogging() {
        const val BACKEND_B: String = "backendB"
    }

    override suspend fun suspendSuccess(): String {
        return "Hello World from backend B"
    }

    override suspend fun suspendFailure(): String {
        throw IOException("BAM!")
    }

    override suspend fun suspendTimeout(): String {
        delay(Duration.ofSeconds(10))
        return "Hello World from backend B"
    }

    override fun flowSuccess(): Flow<String> {
        return flowOf("Hello", "World")
    }

    @Bulkhead(name = BACKEND_B)
    override fun flowFailure(): Flow<String> {
        return flowOf("Hello", "World")
            .onStart { throw IOException("BAM!") }
    }

    override fun flowTimeout(): Flow<String> {
        return flow {
            delay(Duration.ofSeconds(10))
            emit("Hello World from backend B")
        }
    }
}
