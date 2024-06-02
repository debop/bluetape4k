package io.bluetape4k.workshop.bucket4j

import io.bluetape4k.logging.KLogging
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RateLimiterWebfluxApplication {

    companion object: KLogging()

}

fun main(vararg args: String) {
    runApplication<RateLimiterWebfluxApplication>(*args) {
        webApplicationType = WebApplicationType.REACTIVE
    }
}
