package io.bluetape4k.io.avro

import io.bluetape4k.logging.KLogging
import net.datafaker.Faker

abstract class AbstractAvroTest {

    companion object : KLogging() {
        val faker = Faker()
    }

}
