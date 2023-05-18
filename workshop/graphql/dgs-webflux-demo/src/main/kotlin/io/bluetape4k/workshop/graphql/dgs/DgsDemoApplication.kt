package io.bluetape4k.workshop.graphql.dgs

import io.bluetape4k.infra.graphql.dgs.scalars.InstantScalar
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackageClasses = [InstantScalar::class, DgsDemoApplication::class])
class DgsDemoApplication

fun main(vararg args: String) {
    runApplication<DgsDemoApplication>(*args)
}
