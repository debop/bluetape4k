package io.bluetape4k.workshop.application.event

import io.bluetape4k.logging.KLogging
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
class EventApplication {
    companion object: KLogging()
}

fun main(vararg args: String) {
    runApplication<EventApplication>(*args) {
        webApplicationType = WebApplicationType.REACTIVE
    }
}
