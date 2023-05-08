package io.bluetape4k.workshop.application.event.custom

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class CustomEventController(
    private val customEventPublisher: CustomEventPublisher,
): CoroutineScope by CoroutineScope(CoroutineName("custom-event") + Dispatchers.IO) {

    companion object: KLogging()

    @GetMapping("/event")
    suspend fun event(@RequestParam(name = "message") message: String): String {
        customEventPublisher.publish(message)
        delay(100)
        customEventPublisher.publish(message)

        log.debug { "Finish to publish event. message=$message" }
        return "Finished"
    }
}
