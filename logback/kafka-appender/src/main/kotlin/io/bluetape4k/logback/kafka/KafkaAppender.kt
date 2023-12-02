package io.bluetape4k.logback.kafka

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.spi.AppenderAttachableImpl
import io.bluetape4k.logback.kafka.exporter.ExportFallback
import io.bluetape4k.support.packageName
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.KafkaException
import org.apache.kafka.common.serialization.ByteArraySerializer
import java.util.concurrent.ConcurrentLinkedQueue

class KafkaAppender<E>: KafkaAppenderOptions<E>() {

    companion object {
        internal val KAFKA_LOGGER_PREFIX = Producer::class.packageName.replaceFirst(".producer", "", true)
    }

    private val attacher = AppenderAttachableImpl<E>()
    private val queue = ConcurrentLinkedQueue<E>()

    private var producerProvider: ProducerProvider? = null

    private val exportFallback = ExportFallback<E> { event, exception ->
        if (exception != null) {
            addWarn("Fail to export log to Kafka: ${exception.message}", exception)
            // KafkaProducer 자체의 문제라면 새롭게 생성하게 한다. (Broker 장애로 Producer를 새롭게 생성해야 하는 경우가 있다)
            if (producerProvider?.isInitialized == true) {
                producerProvider?.reset()
            }
        }
        attacher.appendLoopOnAppenders(event)
    }

    init {
        println("KafkaAppender created.")
    }

    override fun doAppend(event: E) {
        // println("doAppend. event=$event")

        // Kafka 관련 로그가 있다면, 그 로그 정보를 먼저 Kafka로 로그를 출력한다.
        drainQueue()

        if (isKafkaProducerLog(event)) {
            // KafkaProducer가 발생시킨 로그라면 바로 보내지 않고, queue에 담았다가 다음 로그가 발생할 때 drainQueue에서 모아서 보낸다.
            queue.offer(event)
        } else {
            super.doAppend(event)
        }
    }

    private fun drainQueue() {
        var event = queue.poll()
        while (event != null) {
            append(event)
            event = queue.poll()
        }
    }

    private fun isKafkaProducerLog(event: E): Boolean {
        return event is ILoggingEvent && event.loggerName.startsWith(KAFKA_LOGGER_PREFIX)
    }

    override fun append(event: E) {
        val payload = encoder?.encode(event) ?: return
        val key = keyProvider?.get(event)
        val timestamp: Long? = if (needAppendTimestamp) getTimestamp(event) else null
        val record = ProducerRecord(topic, partition, timestamp, key, payload)

        val producer = producerProvider?.get()

        if (producer != null) {
            logExporter?.export(producer, record, event, exportFallback)
        } else {
            exportFallback.handleException(event, null)
        }
    }

    private fun getTimestamp(event: E): Long = when (event) {
        is ILoggingEvent -> event.timeStamp
        else             -> System.currentTimeMillis()
    }

    override fun start() {
        println("starting KafkaAppender...")
        // only error free appenders should be activated
        if (!checkOptions()) {
            return
        }

        producerProvider = ProducerProvider()
        super.start()
        println("started KafkaAppender")
    }

    override fun stop() {
        super.stop()
        queue.clear()
        if (producerProvider?.isInitialized == true) {
            try {
                producerProvider?.get()?.close()
            } catch (e: KafkaException) {
                addWarn("Fail to shutdown kafka producer: ${e.message}", e)
            }
            producerProvider = null
        }
    }

    override fun addAppender(newAppender: Appender<E>) {
        attacher.addAppender(newAppender)
    }

    override fun iteratorForAppenders(): MutableIterator<Appender<E>> {
        return attacher.iteratorForAppenders()
    }

    override fun getAppender(name: String?): Appender<E> {
        return attacher.getAppender(name)
    }

    override fun detachAndStopAllAppenders() {
        attacher.detachAndStopAllAppenders()
    }

    override fun detachAppender(name: String): Boolean {
        return attacher.detachAppender(name)
    }

    override fun detachAppender(appender: Appender<E>): Boolean {
        return attacher.detachAppender(appender)
    }

    override fun isAttached(appender: Appender<E>): Boolean {
        return attacher.isAttached(appender)
    }

    internal fun createProducer(): Producer<ByteArray?, ByteArray?> {
        addInfo("Create Kafka Producer for Logging with config: $producerConfig")
        return KafkaProducer(producerConfig, ByteArraySerializer(), ByteArraySerializer())
    }

    private inner class ProducerProvider {

        @Volatile
        private var producer: Producer<ByteArray?, ByteArray?>? = null

        val isInitialized: Boolean
            get() = producer != null

        fun get(): Producer<ByteArray?, ByteArray?>? {
            var result = this.producer

            if (result == null) {
                synchronized(this) {
                    result = this.producer
                    if (result == null) {
                        result = initialize()
                        this.producer = result
                    }
                }
            }
            return result
        }

        fun reset() {
            synchronized(this) {
                runCatching { producer?.close() }
                producer = null
            }
        }

        private fun initialize(): Producer<ByteArray?, ByteArray?>? {
            return try {
                createProducer()
            } catch (e: Exception) {
                addError("fail to create Kafka Producer", e)
                null
            }
        }
    }
}
