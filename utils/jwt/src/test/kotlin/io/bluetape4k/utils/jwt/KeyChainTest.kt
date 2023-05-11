package io.bluetape4k.utils.jwt

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.jsonwebtoken.security.Keys
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.jupiter.api.RepeatedTest

class KeyChainTest: AbstractJwtTest() {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 3
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `RSA Algorithm이 같아도 매번 다른 KeyPair를 가진다`() {
        rsaAlgorithm.forEach { algorithm ->
            log.debug { "algorithm=$algorithm" }
            val keyPair1 = Keys.keyPairFor(algorithm)
            val keyPair2 = Keys.keyPairFor(algorithm)

            keyPair2.public shouldNotBeEqualTo keyPair1.public
            keyPair2.private shouldNotBeEqualTo keyPair1.private
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `convert KeyChain to DTO`() {
        rsaAlgorithm.forEach { algorithm ->
            log.debug { "algorithm=$algorithm" }

            val keyChain = KeyChain(algorithm)
            val dto = keyChain.toDto()
            val actual = dto.toKeyChain()
            actual shouldBeEqualTo keyChain
        }
    }
}
