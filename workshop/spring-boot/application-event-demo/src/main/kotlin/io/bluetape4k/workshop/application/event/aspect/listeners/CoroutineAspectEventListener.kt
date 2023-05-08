package io.bluetape4k.workshop.application.event.aspect.listeners

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.application.event.aspect.AspectEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.mono
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class CoroutineAspectEventListener {

    companion object: KLogging()

    @EventListener(classes = [AspectEvent::class])
    fun handleEvent(event: AspectEvent) = mono(Dispatchers.IO) {
        doHandleEvent(event)
    }

    private suspend fun doHandleEvent(event: AspectEvent) {
        log.debug { "Handle aspect event by coroutine listener. $event" }
    }
}
