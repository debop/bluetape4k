package io.bluetape4k.workshop.graphql.dgs

import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebFlux
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@EnableAutoConfiguration
@AutoConfigureWebFlux
abstract class AbstractDgsTest {

    companion object: KLogging() {
        @JvmStatic
        val faker = Fakers.faker
    }
}
