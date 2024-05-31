package io.bluetape4k.data.redis

import io.bluetape4k.idgenerators.snowflake.Snowflakers
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.storage.RedisServer

abstract class AbstractRedisTest {

    companion object: KLogging() {

        @JvmStatic
        val redis: RedisServer by lazy { RedisServer.Launcher.redis }

        @JvmStatic
        val faker = Fakers.faker

        @JvmStatic
        protected fun randomName(): String =
            "bluetape4k:${faker.name().username()}:${Snowflakers.Global.nextId()}"

        @JvmStatic
        protected fun randomString(size: Int = 2048): String =
            Fakers.fixedString(size)

    }
}
