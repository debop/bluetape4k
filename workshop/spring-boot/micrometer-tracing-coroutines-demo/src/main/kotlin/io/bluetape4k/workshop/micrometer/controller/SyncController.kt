package io.bluetape4k.workshop.micrometer.controller

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.info
import io.bluetape4k.workshop.micrometer.model.Todo
import io.bluetape4k.workshop.micrometer.service.SyncService
import io.micrometer.observation.annotation.Observed
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 동기방식의 Controller 에 대해서는 기본적으로 Observation 이 적용됩니다.
 */
@RestController
@RequestMapping("/sync")
class SyncController(
    private val syncService: SyncService,
) {

    companion object: KLogging()

    @Observed(contextualName = "sync-get-name-at-controller")
    @GetMapping("/name")
    fun getName(): String {
        log.info { "Get name in sync" }
        return syncService.getName()
    }

    @Observed(contextualName = "sync-get-todo-at-controller")
    @GetMapping("/todos/{id}")
    fun getTodo(@PathVariable(name = "id", required = true) id: Int): Todo? {
        log.debug { "Get todo[$id] in sync" }
        Thread.sleep(100)
        return syncService.getTodo(id).apply {
            Thread.sleep(100)
        }
    }
}
