package io.bluetape4k.codec

import io.bluetape4k.junit5.concurrency.MultithreadingTester
import io.bluetape4k.junit5.coroutines.MultiJobTester
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.toUtf8Bytes
import io.bluetape4k.support.toUtf8String
import io.bluetape4k.support.toUuid
import io.bluetape4k.utils.Runtimex
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import java.util.*

@RandomizedTest
class BytesBase62Test {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `encode decode small length string`() {
        val originStr = Fakers.fixedString(16)

        val encoded = BytesBase62.encode(originStr.toUtf8Bytes())
        val decodedStr = BytesBase62.decode(encoded).toUtf8String()

        log.debug { "encoded=$encoded" }
        log.debug { "decoded=$decodedStr, origin=$originStr" }

        decodedStr shouldBeEqualTo originStr
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `encode decode large length string`() {
        val originStr = Fakers.faker.lorem().paragraph()

        val encoded = BytesBase62.encode(originStr.toUtf8Bytes())
        val decodedStr = BytesBase62.decode(encoded).toUtf8String()

        log.debug { "encoded=$encoded, origin=$originStr" }

        decodedStr shouldBeEqualTo originStr
    }

    @Test
    fun `encode and decode in multi-thread`() {
        MultithreadingTester()
            .numThreads(2 * Runtimex.availableProcessors)
            .roundsPerThread(4)
            .add {
                val originStr = Fakers.faker.lorem().sentence()

                val encoded = BytesBase62.encode(originStr.toUtf8Bytes())
                val decodedStr = BytesBase62.decode(encoded).toUtf8String()

                log.debug { "encoded=$encoded, decoded=$decodedStr" }
                decodedStr shouldBeEqualTo originStr
            }
            .run()
    }

    @Test
    fun `encode base62 in multi-job`() = runTest {
        MultiJobTester()
            .numJobs(2 * Runtimex.availableProcessors)
            .roundsPerJob(4)
            .add {
                val originStr = Fakers.faker.lorem().sentence()

                val encoded = BytesBase62.encode(originStr.toUtf8Bytes())
                val decodedStr = BytesBase62.decode(encoded).toUtf8String()

                log.debug { "encoded=$encoded" }
                log.debug { "decoded=$decodedStr, origin=$originStr" }

                decodedStr shouldBeEqualTo originStr
            }
            .run()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `encode uuid as byte array`() {
        val origin = UUID.randomUUID()

        val encoded = origin.encodeBase62String()
        val decodedUuid = encoded.decodeBase62AsUUID()

        log.debug { "encoded=$encoded, origin=$origin" }
        decodedUuid shouldBeEqualTo origin
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `compare Base62 and BytesBase62`() {
        val uuid = UUID.randomUUID()

        val encodedNum = uuid.encodeBase62()
        val encodedBytes = uuid.encodeBase62String()

        val decodedNum = encodedNum.decodeBase62().toUuid()
        val decodedBytes = encodedBytes.decodeBase62AsUUID()

        log.debug { "encodedNum  =$encodedNum" }
        log.debug { "encodedBytes=$encodedBytes" }

        // encodedNum shouldBeEqualTo encodedBytes
        decodedNum shouldBeEqualTo uuid
        decodedBytes shouldBeEqualTo uuid
    }
}
