package io.bluetape4k.workshop.graphql.dgs

import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.uninitialized
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@EnableAutoConfiguration
@AutoConfigureMockMvc
abstract class AbstractDgsTest {

    companion object: KLogging() {
        @JvmStatic
        val faker = Fakers.faker
    }

    @Autowired
    protected val mvc: MockMvc = uninitialized()
}
