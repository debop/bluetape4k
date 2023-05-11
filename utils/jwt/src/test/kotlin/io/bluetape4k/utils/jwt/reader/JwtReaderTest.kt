package io.bluetape4k.utils.jwt.reader

import io.bluetape4k.junit5.random.RandomValue
import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.utils.jwt.AbstractJwtTest
import io.bluetape4k.utils.jwt.provider.JwtProviderFactory
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterThan
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

@RandomizedTest
class JwtReaderTest: AbstractJwtTest() {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 3
    }

    private val jwtProvider = JwtProviderFactory.default()


    @RepeatedTest(REPEAT_SIZE)
    fun `read jwt`(@RandomValue claim1: String, @RandomValue claim2: String, @RandomValue claim3: Long) {

        val now = Date()
        val nowSeconds = now.time / 1000L * 1000L

        val jwt = jwtProvider.composer()
            .header("x-author", "debop")
            .claim("claim1", claim1)
            .claim("claim2", claim2)
            .claim("claim3", claim3)
            .issuer("kommons")
            .issuedAt(now)
            .notBefore(now)
            .expirationAfterMinutes(60L)
            .compose()

        log.debug { "jwt length=${jwt.length}" }
        log.debug { "jwt=$jwt" }

        val reader = jwtProvider.parse(jwt)
        reader.header<String>("x-author") shouldBeEqualTo "debop"
        reader.claim<String>("claim1") shouldBeEqualTo claim1
        reader.claim<String>("claim2") shouldBeEqualTo claim2
        reader.claim<Long>("claim3") shouldBeEqualTo claim3

        reader.issuer shouldBeEqualTo "kommons"
        reader.issuedAt.time shouldBeEqualTo nowSeconds
        reader.notBefore.time shouldBeEqualTo nowSeconds
        reader.expiration.time shouldBeGreaterThan now.time
    }

    /**
     * java time은 기본적으로 제공하지 않으니, Epoch 을 사용하세요.
     *
     */
    @Test
    fun `claim value with java time`() {
        val createdAt = Instant.now().toEpochMilli()
        val jwt = jwtProvider.compose {
            claim("createdAt", createdAt)
        }

        val reader = jwtProvider.parse(jwt)
        reader.claim<Long>("createdAt") shouldBeEqualTo createdAt
    }
}
