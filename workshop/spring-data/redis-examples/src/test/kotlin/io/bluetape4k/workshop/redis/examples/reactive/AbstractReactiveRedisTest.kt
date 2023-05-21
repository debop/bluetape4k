package io.bluetape4k.workshop.redis.examples.reactive

import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.redis.examples.RedisApplication
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [RedisApplication::class, ReactiveRedisConfiguration::class])
abstract class AbstractReactiveRedisTest {

    companion object: KLogging()

}
