package io.bluetape4k.utils.jwt

import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.jsonwebtoken.SignatureAlgorithm
import net.datafaker.Faker

abstract class AbstractJwtTest {

    companion object: KLogging() {

        const val PLAIN_TEXT = "Hello, World! 동해물과 백두산이 # debop@bluetape4k.io"

        val faker = Faker()

        fun randomString(maxSize: Int = 4096): String {
            return Fakers.randomString(maxSize / 2, maxSize, true)
        }
    }

    protected val rsaAlgorithm: List<SignatureAlgorithm> = SignatureAlgorithm.values().filter { it.isRsa }

}
