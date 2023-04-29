package io.bluetape4k.examples.redisson.coroutines.objects

import io.bluetape4k.data.redis.redisson.coroutines.awaitSuspending
import io.bluetape4k.examples.redisson.coroutines.AbstractRedissonCoroutineTest
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.atomicfu.atomic
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.junit.jupiter.api.Test


class TopicExamples: AbstractRedissonCoroutineTest() {

    companion object: KLogging()

    @Test
    fun `add topic listener`() = runSuspendWithIO {
        val topic = redisson.getTopic(randomName())
        val receivedCount = atomic(0)

        // topic 예 listener를 등록합니다.
        // listener id 를 반환한다.
        val listenerId1 = topic.addListenerAsync(String::class.java) { channel, msg ->
            println("Listener1: channel[$channel] received: $msg")
            receivedCount.incrementAndGet()
        }.awaitSuspending()

        val listenerId2 = topic.addListenerAsync(String::class.java) { channel, msg ->
            println("Listener2: channel[$channel] received: $msg")
            receivedCount.incrementAndGet()
        }.awaitSuspending()

        log.debug { "Listener listener1 Id=$listenerId1, listener2 Id=$listenerId2" }

        // topic 에 메시지 전송
        topic.publishAsync("message-1").awaitSuspending()
        topic.publishAsync("message-2").awaitSuspending()

        // topic 에 listener가 2개, 메시지 2개 전송 
        await until { receivedCount.value == 2 * 2 }

        topic.removeAllListenersAsync().awaitSuspending()
    }
}
