package io.bluetape4k.workshop.bucket4j

import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class CaffeineApplication

fun main(vararg args: String) {
    runApplication<CaffeineApplication>(*args) {
        webApplicationType = WebApplicationType.SERVLET
    }
}
