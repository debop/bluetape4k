package io.bluetape4k.workshop.spring.security

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinApplication

fun main(vararg args: String) {
    runApplication<KotlinApplication>(*args)
}
