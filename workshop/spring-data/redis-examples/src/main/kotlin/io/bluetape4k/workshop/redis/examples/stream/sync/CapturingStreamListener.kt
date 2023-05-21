package io.bluetape4k.workshop.redis.examples.stream.sync

import io.bluetape4k.logging.KLogging
import kotlinx.atomicfu.atomic
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.stream.StreamListener
import java.util.concurrent.LinkedBlockingDeque

/**
 * Spring Data Redis에서 제공하는 Redis Stream 을 읽기 위한 [StreamListener]의 구현체
 */
class CapturingStreamListener: StreamListener<String, MapRecord<String, String, String>> {

    companion object: KLogging()

    private val counter = atomic(0)
    private val dequeue = LinkedBlockingDeque<MapRecord<String, String, String>>()

    /**
     * Redis Stream 에 값이 존재하면, [MapRecord]을 받아서 처리합니다.
     *
     * @param message
     */
    override fun onMessage(message: MapRecord<String, String, String>) {
        dequeue.add(message)
        counter.incrementAndGet()
    }

    val receivedRecordCount by counter

    fun take(): MapRecord<String, String, String> {
        return dequeue.take()
    }
}
