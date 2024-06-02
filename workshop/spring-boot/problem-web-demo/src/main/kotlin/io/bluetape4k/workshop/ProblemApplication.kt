package io.bluetape4k.workshop

import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProblemApplication

fun main(vararg args: String) {
    runApplication<ProblemApplication>(*args) {
        webApplicationType = WebApplicationType.REACTIVE
    }
}
