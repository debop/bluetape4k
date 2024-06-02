package io.bluetape4k.spring.webflux.config

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.uninitialized
import io.bluetape4k.utils.Runtimex
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@SpringBootTest(classes = [CustomWebClientConfig::class])
class CustomWebClientConfigTest {

    companion object: KLogging()

    @Autowired
    private val webClient: WebClient = uninitialized()

    @Autowired
    private val httpConnector: ReactorClientHttpConnector = uninitialized()

    @Test
    fun `create webclient using custom thread pool`() {
        httpConnector.shouldNotBeNull()
        webClient.shouldNotBeNull()
    }

    @Test
    fun `get by custom webclient`() = runSuspendWithIO {
        val response = webClient.get()
            .uri("https://www.google.com")
            .retrieve()
            .awaitBody<String>()

        // 로그의 Thread name 을 확인해야 합니다.
        // 로그 상에 Thread 명에 `web-client-thread-` 가 있다면 custom thread pool 을 사용한 것이다.
        //[      web-client-thread-1] o.s.w.r.f.client.ExchangeFunctions
        log.debug { "구글 샤이트=$response" }
    }

    @Test
    fun `async get by custom webclient`() = runSuspendWithIO {
        val task = List(2 * Runtimex.availableProcessors) {
            async {
                webClient.get()
                    .uri("https://www.google.com")
                    .retrieve()
                    .awaitBody<String>()
            }
        }
        task.awaitAll()
    }
}
