package io.bluetape4k.workshop.application.event.custom.listeners

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.application.event.custom.CustomEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class CustomEventListener: ApplicationListener<CustomEvent> {

    companion object: KLogging()

    override fun onApplicationEvent(event: CustomEvent) {
        runBlocking(Dispatchers.IO) {
            saveEvent(event)
        }
    }

    suspend fun saveEvent(event: CustomEvent) {
        log.debug { "Handle custom event. $event" }
        delay(100)
    }
}
