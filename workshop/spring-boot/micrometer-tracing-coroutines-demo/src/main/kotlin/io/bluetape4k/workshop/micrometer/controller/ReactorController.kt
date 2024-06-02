package io.bluetape4k.workshop.micrometer.controller

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.micrometer.model.Todo
import io.bluetape4k.workshop.micrometer.service.ReactorService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Duration

@RestController
@RequestMapping("/reactor")
class ReactorController(
    private val reactorService: ReactorService,
) {
    companion object: KLogging()

    @GetMapping("/name")
    fun getName(): Mono<String> {
        return Mono
            .delay(Duration.ofMillis(100))
            .flatMap { reactorService.getName() }
    }

    @GetMapping("/todos/{id}")
    fun getTodo(@PathVariable(name = "id", required = true) id: Int): Mono<Todo> {
        log.debug { "Get todo[$id] in reactive" }
        return Mono.delay(Duration.ofMillis(100))
            .flatMap { reactorService.getTodoById(id) }
            .doOnEach { log.debug { "Get todo by id[$id] in reactive" } }
            .delayElement(Duration.ofMillis(100))
    }
}
