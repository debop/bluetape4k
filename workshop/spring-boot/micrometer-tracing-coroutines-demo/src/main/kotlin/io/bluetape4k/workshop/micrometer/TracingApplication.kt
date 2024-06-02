package io.bluetape4k.workshop.micrometer

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.infrastructure.ZipkinServer
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TracingApplication {

    companion object: KLogging() {
        @JvmStatic
        val zipkinServer = ZipkinServer.Launcher.zipkin

        val zipkinUrl: String get() = zipkinServer.url
    }

}

fun main(vararg args: String) {
    runApplication<TracingApplication>(*args) {
        webApplicationType = WebApplicationType.REACTIVE
    }
}
