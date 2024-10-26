package io.bluetape4k.workshop.redis.examples

import io.bluetape4k.logging.KLogging
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [io.bluetape4k.workshop.redis.examples.RedisApplication::class])
abstract class AbstractRedisTest {

    companion object: KLogging()
}
