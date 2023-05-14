package io.bluetape4k.infra.graphql

import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging

abstract class AbstractGraphqlTest {

    companion object: KLogging() {
        @JvmStatic
        val faker = Fakers.faker

        @JvmStatic
        fun randomString() = Fakers.randomString(16, 256)
    }
}
