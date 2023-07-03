package io.bluetape4k.workshop.bucket4j.caffeine

import io.bluetape4k.logging.KLogging
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class CaffeineApplication {

    companion object: KLogging()

}

fun main(vararg args: String) {
    runApplication<CaffeineApplication>(*args) {
        webApplicationType = WebApplicationType.REACTIVE
    }
}
