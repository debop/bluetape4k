package io.bluetape4k.utils.jwt.provider

import io.bluetape4k.core.LibraryName
import io.bluetape4k.junit5.concurrency.MultithreadingTester
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.utils.jwt.AbstractJwtTest
import io.bluetape4k.utils.jwt.codec.Lz4Codec
import io.bluetape4k.utils.jwt.reader.isExpired
import io.bluetape4k.utils.jwt.repository.KeyChainRepository
import io.bluetape4k.utils.jwt.repository.inmemory.InMemoryKeyChainRepository
import io.bluetape4k.utils.jwt.utils.dateOfEpochSeconds
import io.bluetape4k.utils.jwt.utils.epochSeconds
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.test.assertFailsWith

@Execution(ExecutionMode.SAME_THREAD)
abstract class AbstractJwtProviderTest: AbstractJwtTest() {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
        private val compressCodec = Lz4Codec()
    }

    protected val repository: KeyChainRepository = InMemoryKeyChainRepository()

    abstract val provider: JwtProvider

    @BeforeEach
    fun beforeEach() {
        repository.clear()
        provider.rotate()
    }

    @Test
    fun `명시적으로 rotate 하지 않는 한 Current KeyChain은 같아야 한다`() {
        val keyChain1 = provider.currentKeyChain()
        val keyChain2 = provider.currentKeyChain()

        keyChain2 shouldBeEqualTo keyChain1

        Thread.sleep(10)
        provider.currentKeyChain() shouldBeEqualTo keyChain1
    }

    @Test
    fun `rotate 를 하면 Current KeyChain이 변경되어야 한다`() {
        val keyChain1 = provider.currentKeyChain()

        // rotate 하면 key chain 이 변경됩니다.
        provider.rotate()

        val keyChain2 = provider.currentKeyChain()

        keyChain2 shouldNotBeEqualTo keyChain1
        provider.currentKeyChain() shouldBeEqualTo keyChain2
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `compose jwt token`() {
        val now = Date()
        val jwt = provider.compose {
            claim("author", "debop")
            issuer = LibraryName
            issuedAt = now
            compressionCodec = compressCodec
        }

        val reader = provider.parse(jwt)
        reader.issuer shouldBeEqualTo LibraryName
        reader.issuedAt shouldBeEqualTo dateOfEpochSeconds(now.epochSeconds)
        reader.claim<String>("author") shouldBeEqualTo "debop"
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `compose jwt in concurrency`() {

        val jwts = ArrayDeque<String>()
        MultithreadingTester()
            .numThreads(4)
            .roundsPerThread(4)
            .add {
                val jwt = provider.compose {
                    claim("author", "debop")
                    claim("service", LibraryName)
                    issuer = LibraryName
                    compressionCodec = compressCodec
                }

                jwts.add(jwt)
                // Thread.sleep(10L)
            }
            .run()

        Thread.sleep(10L)

        jwts.size shouldBeEqualTo 4 * 4
        val uniqueJwts = jwts.distinct()
        uniqueJwts.forEach { jwt ->
            log.debug { "jwt=$jwt" }
        }
        uniqueJwts.size shouldBeEqualTo 1
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `삭제된 KeyChain으로 구성된 jwt는 parsing이 실패해야 한다`() {
        Assumptions.assumeTrue { provider is DefaultJwtProvider }

        val jwt = provider.compose {
            claim("author", "debop")
            expirationAfterSeconds = 30
        }
        val reader = provider.parse(jwt)
        log.debug { "kid=${reader.kid}" }
        reader.isExpired.shouldBeFalse()

        // KeyChain 이 변경되었으므로,
        provider.rotate()

        val reader2 = provider.parse(jwt)
        log.debug { "kid=${reader2.kid}" }

        // 오래된 key chanin을 삭제하도록 충분히 rotate 한다
        repeat(repository.capacity * 2) {
            provider.rotate()
        }

        // jwt 발급에 쓰인 KeyChain을 provider의 repository에 없으므로, parsing 이 되지 않는다.
        assertFailsWith<SecurityException> {
            provider.parse(jwt)
        }
    }
}
