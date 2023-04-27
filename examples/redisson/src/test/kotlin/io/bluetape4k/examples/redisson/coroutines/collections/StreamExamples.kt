package io.bluetape4k.examples.redisson.coroutines.collections

import io.bluetape4k.data.redis.redisson.ackAllAsync
import io.bluetape4k.data.redis.redisson.streamAddArgsOf
import io.bluetape4k.examples.redisson.coroutines.AbstractRedissonCoroutineTest
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.kotlinx.coroutines.support.awaitSuspending
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.utils.idgenerators.uuid.TimebasedUuid
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test
import org.redisson.api.RStream
import org.redisson.api.StreamMessageId
import java.util.concurrent.TimeUnit

/**
 * [RStream] Examples
 *
 * 참고: [Redis Stream for Java](https://redisson.org/articles/redis-streams-for-java.html)
 */
class StreamExamples: AbstractRedissonCoroutineTest() {

    companion object: KLogging()

    private val groupName = "group-" + TimebasedUuid.nextBase62String()
    private val consumerName1 = "consumer-" + TimebasedUuid.nextBase62String()
    private val consumerName2 = "consumer-" + TimebasedUuid.nextBase62String()

    @Test
    fun `stream usage`() = runSuspendWithIO {
        val stream: RStream<String, Int> = redisson.getStream(randomName())

        // Consumer group 을 만든다
        stream.createGroup(groupName)

        // 메시지를 전송한다
        val messageId1 = stream.addAsync(streamAddArgsOf("1", 1)).awaitSuspending()
        val messageId2 = stream.addAsync(streamAddArgsOf("2", 2)).awaitSuspending()
        log.debug { "메시지 전송, messageId1=$messageId1" }
        log.debug { "메시지 전송, messageId2=$messageId2" }

        // 2개의 메시지를 받는다
        val map1 = stream.readGroupAsync(
            groupName,
            consumerName1,
            StreamMessageId.NEVER_DELIVERED
        ).awaitSuspending()

        map1.keys.forEach { messageId ->
            log.debug { "메시지 수신, messageId=$messageId" }
        }

        map1.keys shouldHaveSize 2
        map1.keys shouldBeEqualTo setOf(messageId1, messageId2)

        // 2개의 메시지를 읽었다고 ack 보냄 (전송완료)
        stream.ackAsync(groupName, *map1.keys.toTypedArray()).awaitSuspending()

        // 메시지를 기다린다.
        val consumerJob = scope.launch {
            // 1개의 메시지를 받는다
            val map2 = stream.readGroupAsync(
                groupName,
                consumerName2,
                10,
                TimeUnit.SECONDS,
                StreamMessageId.NEVER_DELIVERED
            ).awaitSuspending()

            // 1개의 메시지를 받았다
            map2.keys shouldHaveSize 1
            val msgId = map2.keys.first()
            log.debug { "메시지 수신, messageId=$msgId" }
            map2[msgId]!! shouldBeEqualTo mapOf<String, Int>("3" to 3, "4" to 4)

            stream.ackAllAsync(groupName, map2.keys).awaitSuspending() shouldBeEqualTo 1L
        }

        // 새로운 메시지 1개를 전송한다
        val messageId3 = stream.addAsync(streamAddArgsOf("3" to 3, "4" to 4)).awaitSuspending()
        log.debug { "메시지 전송, messageId3=$messageId3" }
        delay(10)
        consumerJob.join()

        stream.deleteAsync().awaitSuspending()

        redisson.getTimeSeries<Int, String>(randomName())
    }
}
