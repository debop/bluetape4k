package io.bluetape4k.workshop.bucket4j.hazelcast

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.atomicfu.atomic
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/coroutines")
class CoroutineController {

    companion object: KLogging()

    private val helloCounter = atomic(0)
    private val worldCounter = atomic(0)

    @GetMapping("/hello")
    suspend fun hello(): String {
        log.debug { "hello called. call count=${helloCounter.incrementAndGet()}" }
        return "Hello World"
    }

    @GetMapping("/world")
    suspend fun world(): String {
        log.debug { "world called. call count=${worldCounter.incrementAndGet()}" }
        return "Hello World"
    }
}
