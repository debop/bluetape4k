package io.bluetape4k.workshop.bucket4j

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.reactor.mono
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/reactive")
class ReactiveController {

    companion object: KLogging()

    private val helloCounter = atomic(0)
    private val worldCounter = atomic(0)

    @GetMapping("/hello")
    fun hello(): Mono<String> = mono {
        log.debug { "hello called. call count=${helloCounter.incrementAndGet()}" }
        "Hello World"
    }

    @GetMapping("/world")
    fun world(): Mono<String> = mono {
        log.debug { "world called. call count=${worldCounter.incrementAndGet()}" }
        "Hello World"
    }
}
