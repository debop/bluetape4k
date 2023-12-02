package io.bluetape4k.logback.kafka.exporter

import io.mockk.Called
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.errors.TimeoutException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException

class DefaultLogExporterTest {

    private val producer = mockk<Producer<String, String>>(relaxed = true)
    private val fallback = mockk<ExportFallback<String>>(relaxed = true)
    private val exporter = DefaultLogExporter()

    private val topicPartition = TopicPartition("topic", 0)
    private val record = ProducerRecord<String, String>("topic", 0, null, "msg")
    private val recordMetadata = RecordMetadata(
        topicPartition,
        0,
        0,
        System.currentTimeMillis(),
        32,
        64
    )


    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
    }

    @Test
    fun `export 가 실패해도 예외가 발생하지 않으면 fallback은 호출되지 않는다`() {
        val callback = mockk<Callback>("callback")
        every { callback.onCompletion(any(), any()) } answers {
            val args = invocation.args
            val metadata = args[0] as RecordMetadata
            callback.onCompletion(metadata, null)
        }
        every { producer.send(record, callback) } returns mockk(relaxed = true)

        exporter.export(producer, record, "msg", fallback)

        verify { fallback wasNot Called }
    }

    @Test
    fun `export가 실패하면서 예외가 발생하면 fallback이 호출된다`() {
        val exception = IOException("BAM!")

        // 참고: https://notwoods.github.io/mockk-guidebook/docs/mockito-migrate/argument-captor/
        val callbackSlot = slot<Callback>()
        every { producer.send(record, capture(callbackSlot)) } returns mockk(relaxed = true)

        exporter.export(producer, record, "msg", fallback)

        callbackSlot.captured.onCompletion(recordMetadata, exception)
        verify { fallback.handleException("msg", exception) }
    }

    @Test
    fun `producer가 TimeoutException 예외가 발생하면 fallback이 호출된다`() {
        val exception = TimeoutException("Timeout!")

        every { producer.send(record, any<Callback>()) } throws exception

        exporter.export(producer, record, "msg", fallback)

        verify { fallback.handleException("msg", exception) }

    }
}
