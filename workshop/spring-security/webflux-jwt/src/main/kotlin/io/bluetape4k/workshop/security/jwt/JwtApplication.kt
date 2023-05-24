package io.bluetape4k.workshop.security.jwt

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JwtApplication

fun main(vararg args: String) {
    runApplication<JwtApplication>(*args)
}
