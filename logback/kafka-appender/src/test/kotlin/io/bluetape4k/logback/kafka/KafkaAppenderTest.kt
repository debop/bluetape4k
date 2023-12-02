package io.bluetape4k.logback.kafka

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.BasicStatusManager
import ch.qos.logback.core.encoder.Encoder
import io.bluetape4k.logback.kafka.KafkaAppender.Companion.KAFKA_LOGGER_PREFIX
import io.bluetape4k.logback.kafka.exporter.LogExporter
import io.bluetape4k.logback.kafka.keyprovider.KeyProvider
import io.bluetape4k.logging.KLogging
import io.mockk.Called
import io.mockk.clearAllMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldHaveSize
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class KafkaAppenderTest {

    companion object: KLogging()

    private val appender = KafkaAppender<ILoggingEvent>()
    private val ctx = LoggerContext()
    private val encoder = mockk<Encoder<ILoggingEvent>>(relaxed = true)
    private val keyProvider = mockk<KeyProvider<ILoggingEvent>>(relaxed = true)
    private val logExporter = mockk<LogExporter>(relaxed = true)

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()

        ctx.setName("testctx")
        ctx.statusManager = BasicStatusManager()


        appender.context = ctx
        appender.name = "KafkaAppenderBase"
        appender.encoder = encoder

        appender.bootstrapServers = "localhost:9092"
        appender.topic = "topic"
        appender.keyProvider = keyProvider
        appender.logExporter = logExporter

        ctx.start()
    }

    @AfterEach
    fun afterEach() {
        ctx.stop()
        appender.stop()
    }

    @Test
    fun `start and stop`() {
        appender.start()
        appender.isStarted.shouldBeTrue()

        appender.stop()
        appender.isStarted.shouldBeFalse()

        ctx.statusManager.copyOfStatusList.shouldBeEmpty()

        confirmVerified(encoder, keyProvider, logExporter)
    }

    @Test
    fun `dont start without topic`() {
        appender.topic = null
        appender.start()
        appender.isStarted.shouldBeFalse()
        ctx.statusManager.copyOfStatusList shouldHaveSize 1
        ctx.statusManager.copyOfStatusList.first().message shouldBeEqualTo "topic is not set"
    }

    @Test
    fun `dont start without kafka bootstrap server`() {
        appender.bootstrapServers = null
        appender.start()
        appender.isStarted.shouldBeFalse()
        ctx.statusManager.copyOfStatusList shouldHaveSize 1
        ctx.statusManager.copyOfStatusList.first().message shouldBeEqualTo "bootstrap.servers is not set"
    }

    @Test
    fun `dont start without encoder`() {
        appender.encoder = null
        appender.start()
        appender.isStarted.shouldBeFalse()
        ctx.statusManager.copyOfStatusList shouldHaveSize 1
        ctx.statusManager.copyOfStatusList.first().message shouldBeEqualTo "encoder is not set"
    }

    @Test
    fun `append uses keyprivider`() {
        every { encoder.encode(any<ILoggingEvent>()) } returns ByteArray(2)
        appender.start()

        val evt = LoggingEvent("fqcn", ctx.getLogger("logger"), Level.ALL, "message", null, null)
        appender.doAppend(evt)

        verify { logExporter.export(any<Producer<ByteArray?, ByteArray?>>(), any(), evt, any()) }
        verify { keyProvider.get(evt) }
        verify { logExporter.export(any<Producer<ByteArray?, ByteArray?>>(), any(), evt, any()) }
    }

    @Test
    fun `append use pre setted partition`() {
        every { encoder.encode(any<ILoggingEvent>()) } returns ByteArray(2)
        // 참고: https://notwoods.github.io/mockk-guidebook/docs/mockito-migrate/argument-captor/
        val producerRecordCaptor = slot<ProducerRecord<ByteArray?, ByteArray?>>()
        appender.partition = 1
        appender.start()

        val evt = LoggingEvent("fqcn", ctx.getLogger("logger"), Level.ALL, "message", null, null)
        appender.doAppend(evt)

        verify {
            logExporter.export(
                any<Producer<ByteArray?, ByteArray?>>(),
                capture(producerRecordCaptor),
                evt,
                any()
            )
        }

        val record = producerRecordCaptor.captured
        record.partition() shouldBeEqualTo 1
    }

    @Test
    fun `confirm KAFKA_LOGGER_PREFIX`() {
        KAFKA_LOGGER_PREFIX shouldBeEqualTo "org.apache.kafka.clients"
    }

    @Test
    fun `deferred append`() {
        every { encoder.encode(any<ILoggingEvent>()) } returns ByteArray(2)
        appender.start()

        val deferredEvent = LoggingEvent(
            "fqcn",
            ctx.getLogger("org.apache.kafka.clients.logger"),
            Level.ALL,
            "deferred message",
            null,
            null
        )
        // Kafka가 발생시킨 로그는 바로 보내지 않고, queue에 담았다가 다음 로그가 발생할 때 drainQueue에서 모아서 보낸다.
        appender.doAppend(deferredEvent)
        verify { logExporter wasNot Called }

        val evt = LoggingEvent("fqcn", ctx.getLogger("logger"), Level.ALL, "message", null, null)
        appender.doAppend(evt)

        verify { logExporter.export(any<Producer<ByteArray?, ByteArray?>>(), any(), deferredEvent, any()) }
        verify { logExporter.export(any<Producer<ByteArray?, ByteArray?>>(), any(), evt, any()) }
    }
}
