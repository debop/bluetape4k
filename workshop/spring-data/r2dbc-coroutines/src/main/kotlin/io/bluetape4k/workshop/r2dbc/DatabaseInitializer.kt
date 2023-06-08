package io.bluetape4k.workshop.r2dbc

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import io.bluetape4k.workshop.r2dbc.domain.CommentRepository
import io.bluetape4k.workshop.r2dbc.domain.PostRepository
import kotlinx.coroutines.runBlocking
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class DatabaseInitializer(
    private val postRepo: PostRepository,
    private val commentRepo: CommentRepository,
) {

    companion object: KLogging()

    @EventListener(value = [ApplicationReadyEvent::class])
    fun init() {
        log.info { "Insert new two posts ... " }

        runBlocking {
            postRepo.init()
            commentRepo.init()
        }

        log.info { "Done insert new two posts ..." }
    }
}
