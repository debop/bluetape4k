package io.bluetape4k.workshop.bucket4j.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
class CoroutineController {

    /**
     * `/api/v1/coorutines/~~~` 는 Rate limit를 적용한다
     */
    @GetMapping("/api/v1/coroutines/hello")
    suspend fun helloV1(): String {
        return "Hello World V1 at " + Instant.now().toString()
    }

    /**
     * `/api/v2/coorutines/~~~` 는 Rate limit를 걸지 않는다 (Bucket4j 적용 안함)
     *
     */
    @GetMapping("/api/v2/coroutines/hello")
    suspend fun helloV2(): String {
        return "Hello World V2 at " + Instant.now().toString()
    }
}
