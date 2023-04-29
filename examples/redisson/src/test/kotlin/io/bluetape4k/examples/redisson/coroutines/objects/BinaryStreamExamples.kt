package io.bluetape4k.examples.redisson.coroutines.objects

import io.bluetape4k.data.redis.redisson.coroutines.awaitSuspending
import io.bluetape4k.examples.redisson.coroutines.AbstractRedissonCoroutineTest
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.toUtf8Bytes
import io.bluetape4k.support.toUtf8String
import java.time.Duration
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.RepeatedTest

/**
 * [RBinaryStream] 예제
 *
 * Java implementation of Redis based RBinaryStream object holds sequence of bytes.
 * It extends RBucket interface and size is limited to 512Mb.
 *
 * 참고:
 * * [Binary Stream Holder](https://github.com/redisson/redisson/wiki/6.-distributed-objects/#62-binary-stream-holder)
 */
class BinaryStreamExamples: AbstractRedissonCoroutineTest() {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 3
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `RBinaryStream 사용 예`() = runSuspendWithIO {
        val stream = redisson.getBinaryStream(randomName())

        val contentStr = Fakers.randomString(256, 2048, true)
        val contentBytes = contentStr.toUtf8Bytes()

        stream.setIfAbsentAsync(contentBytes, Duration.ofSeconds(10)).awaitSuspending().shouldBeTrue()
        stream.setAsync(contentBytes).awaitSuspending()

        val loadedBytes = stream.inputStream.readBytes()
        val loadedStr = loadedBytes.toUtf8String()
        loadedStr shouldBeEqualTo contentStr

        // 기존 값을 비교해서 새로운 Bytes 로 대체한다
        val contentBytes2 = Fakers.randomString(100, 500, true).toUtf8Bytes()
        stream.compareAndSetAsync(contentBytes, contentBytes2).awaitSuspending().shouldBeTrue()

        stream.deleteAsync().awaitSuspending()
    }
}
