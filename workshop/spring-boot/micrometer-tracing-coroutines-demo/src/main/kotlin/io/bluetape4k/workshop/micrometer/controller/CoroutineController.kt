package io.bluetape4k.workshop.micrometer.controller

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.micrometer.model.Todo
import io.bluetape4k.workshop.micrometer.service.CoroutineService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Coroutines 에 대해서는 기본적으로 Observation 이 적용되지 않습니다.
 */
@RestController
@RequestMapping("/coroutine")
class CoroutineController(
    private val coroutineService: CoroutineService,
): CoroutineScope by CoroutineScope(Dispatchers.IO) {

    companion object: KLogging()

    @GetMapping("/name")
    suspend fun getName(): String {
        log.debug { "Get name in coroutine" }
        return coroutineService.getName()
    }

    @GetMapping("/todos/{id}")
    suspend fun getTodo(@PathVariable(name = "id", required = true) id: Int): Todo? {
        log.debug { "Get todo[$id] in coroutines" }
        delay(100)
        return coroutineService.getTodo(id).apply {
            delay(100)
        }
    }
}
