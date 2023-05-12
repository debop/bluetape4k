package io.bluetape4k.utils.jwt.provider

import io.bluetape4k.codec.encodeBase62
import io.bluetape4k.core.LibraryName
import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.jwt.AbstractJwtTest
import io.bluetape4k.utils.jwt.utils.dateOfEpochSeconds
import io.bluetape4k.utils.jwt.utils.epochSeconds
import io.jsonwebtoken.UnsupportedJwtException
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertFailsWith

class FixedJwtProviderTest: AbstractJwtTest() {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
    }

    private val provider = JwtProviderFactory.fixed(UUID.randomUUID().encodeBase62())

    @Test
    fun `fixed jwt provider rotate를 수행할 수 없습니다`() {
        assertFailsWith<UnsupportedJwtException> {
            provider.rotate()
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `compose jwt token`() {
        val now = Date()
        val jwt = provider.compose {
            claim("author", "debop")
            issuer = LibraryName
            issuedAt = now
        }

        val reader = provider.parse(jwt)
        reader.issuer shouldBeEqualTo LibraryName
        reader.issuedAt shouldBeEqualTo dateOfEpochSeconds(now.epochSeconds)
        reader.claim<String>("author") shouldBeEqualTo "debop"
    }
}
