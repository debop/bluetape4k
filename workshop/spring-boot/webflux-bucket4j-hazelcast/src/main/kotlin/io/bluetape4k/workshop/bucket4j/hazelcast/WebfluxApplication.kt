package io.bluetape4k.workshop.bucket4j.hazelcast

import io.bluetape4k.logging.KLogging
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class WebfluxApplication {

    companion object: KLogging()

}

fun main(vararg args: String) {
    runApplication<WebfluxApplication>(*args) {
        webApplicationType = WebApplicationType.REACTIVE
    }
}
