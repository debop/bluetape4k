package io.bluetape4k.data.redis

import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.storage.RedisServer

abstract class AbstractRedisTest {

    companion object: KLogging() {
        val redis: RedisServer by lazy { RedisServer.Launcher.redis }
    }

    protected fun randomName(): String = Fakers.randomString(32, 128, true)
}
