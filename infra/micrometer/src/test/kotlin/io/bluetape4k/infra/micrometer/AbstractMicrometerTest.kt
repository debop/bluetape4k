package io.bluetape4k.infra.micrometer

import io.bluetape4k.logging.KLogging
import net.datafaker.Faker

abstract class AbstractMicrometerTest {

    companion object: KLogging() {
        @JvmStatic
        protected val faker = Faker()
    }
}
