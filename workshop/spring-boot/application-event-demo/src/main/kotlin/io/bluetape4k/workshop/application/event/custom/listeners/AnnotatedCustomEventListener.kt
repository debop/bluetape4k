package io.bluetape4k.workshop.application.event.custom.listeners

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.application.event.custom.CustomEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class AnnotatedCustomEventListener {

    companion object: KLogging()

    @EventListener(classes = [CustomEvent::class])
    fun handleEvent(event: CustomEvent) {
        doHandleEvent(event)
    }

    private fun doHandleEvent(event: CustomEvent) {
        log.debug { "Handle custom event by @EventListener. $event" }
        Thread.sleep(100)
    }
}
