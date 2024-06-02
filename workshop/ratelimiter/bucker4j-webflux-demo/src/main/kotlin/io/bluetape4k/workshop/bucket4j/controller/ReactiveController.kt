package io.bluetape4k.workshop.bucket4j.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Instant

@RestController
class ReactiveController {

    /**
     * `/api/v1/reactive/~~~` 는 Rate limit를 적용한다
     */
    @GetMapping("/api/v1/reactive/hello")
    fun helloV1(): Mono<String> {
        return Mono.just("Hello World V1 at " + Instant.now().toString())
    }

    /**
     * `/api/v2/reactive/~~~` 는 Rate limit를 걸지 않는다 (Bucket4j 적용 안함)
     *
     */
    @GetMapping("/api/v2/reactive/hello")
    fun helloV2(): Mono<String> {
        return Mono.just("Hello World V2 at " + Instant.now().toString())
    }
}
