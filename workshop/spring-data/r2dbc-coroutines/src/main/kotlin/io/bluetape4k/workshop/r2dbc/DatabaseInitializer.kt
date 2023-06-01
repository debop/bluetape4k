package io.bluetape4k.workshop.r2dbc

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import io.bluetape4k.workshop.r2dbc.domain.PostRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class DatabaseInitializer(private val repository: PostRepository) {

    companion object: KLogging()

    @EventListener(value = [ApplicationReadyEvent::class])
    fun init() {
        log.info { "Start database initialization ... " }

        log.info { "Done database initialization ..." }
    }
}
