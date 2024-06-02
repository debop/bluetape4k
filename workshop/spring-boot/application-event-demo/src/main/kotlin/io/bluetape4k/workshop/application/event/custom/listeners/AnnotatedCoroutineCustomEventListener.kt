package io.bluetape4k.workshop.application.event.custom.listeners

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.application.event.custom.CustomEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactor.mono
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class AnnotatedCoroutineCustomEventListener {

    companion object: KLogging()

    /**
     * Coroutine 환경에서 EmitValue Listener 작업을 수행할 수 있습니다.
     *
     * `@EventListener` 가 인자가 하나만 있는 함수에 대해서만 적용이 가능한데,
     * suspend 함수는 continuation 인자를 추가하기 때문에 적용할 수 없습니다.
     * 이럴 때에는 함수를 `mono { ... }` 로 변환하면 내부에서 coroutines 환경 하에서 실행이 가능합니다.
     */
    @EventListener(classes = [CustomEvent::class])
    fun handleEvent(event: CustomEvent) = mono(Dispatchers.IO) {
        doHandleEvent(event)
    }

    private suspend fun doHandleEvent(event: CustomEvent) {
        log.debug { "Handle custom event by @EventListener with coroutines. $event" }
        delay(100)
    }
}
