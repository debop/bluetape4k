package io.bluetape4k.workshop.bucket4j

import io.bluetape4k.logging.KLogging
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractRateLimiterApplicationTest {

    companion object: KLogging()

}
