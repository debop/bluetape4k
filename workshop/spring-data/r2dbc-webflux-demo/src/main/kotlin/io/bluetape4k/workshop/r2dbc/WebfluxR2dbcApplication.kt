package io.bluetape4k.workshop.r2dbc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebfluxR2dbcApplication

fun main(vararg args: String) {
    runApplication<WebfluxR2dbcApplication>(*args)
}
