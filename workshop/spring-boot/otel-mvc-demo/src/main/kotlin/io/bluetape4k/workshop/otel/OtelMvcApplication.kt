package io.bluetape4k.workshop.otel

import io.bluetape4k.logging.KLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OtelMvcApplication {

    companion object: KLogging()
}

fun main(vararg args: String) {
    runApplication<OtelMvcApplication>(*args)
}
