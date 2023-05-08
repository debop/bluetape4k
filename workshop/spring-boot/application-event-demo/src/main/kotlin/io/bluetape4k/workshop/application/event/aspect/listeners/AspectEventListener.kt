package io.bluetape4k.workshop.application.event.aspect.listeners

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.application.event.aspect.AspectEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class AspectEventListener {

    companion object: KLogging()

    @EventListener(classes = [AspectEvent::class])
    fun handleEvent(event: AspectEvent) {
        doHandleEvent(event)
    }

    private fun doHandleEvent(event: AspectEvent) {
        log.debug { "Handle aspect event by listener. $event" }
    }
}
