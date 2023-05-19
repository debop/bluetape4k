package io.bluetape4k.workshop.webflux.hibernate.reactive

import io.bluetape4k.logging.KLogging
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication
@EnableWebFlux
class HibernateReactiveApplication {
    companion object: KLogging()
}


fun main(vararg args: String) {
    runApplication<HibernateReactiveApplication>(*args) {
        webApplicationType = WebApplicationType.REACTIVE
    }
}
