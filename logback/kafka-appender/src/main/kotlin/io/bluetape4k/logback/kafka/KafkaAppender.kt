package io.bluetape4k.logback.kafka

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.Context
import ch.qos.logback.core.UnsynchronizedAppenderBase
import ch.qos.logback.core.spi.AppenderAttachable
import ch.qos.logback.core.spi.AppenderAttachableImpl
import io.bluetape4k.logback.kafka.export.ExportFallback
import io.bluetape4k.support.packageName
import org.apache.kafka.clients.producer.KafkaProducer
import java.util.concurrent.ConcurrentLinkedQueue

class KafkaAppender<E>: UnsynchronizedAppenderBase<E>(), AppenderAttachable<E> {

    companion object {
        private val KAFKA_LOGGER_PREFIX = KafkaProducer::class.packageName.replaceFirst("\\.producer$", "", true)
    }

    private val options: KafkaAppenderOptions<E> = KafkaAppenderOptions()

    private val attacher = AppenderAttachableImpl<E>()
    private val queue = ConcurrentLinkedQueue<E>()

    private val exportFallback = ExportFallback<E> { event, _ ->
        attacher.appendLoopOnAppenders(event)
    }

    private val producer: KafkaProducer<ByteArray?, ByteArray>? by lazy {
        runCatching {
            createProducer()
        }.onFailure {
            addError("fail to create Kafka Producer", it)
        }.getOrNull()
    }

    override fun setContext(context: Context) {
        options.context = context
        super.setContext(context)
    }

    override fun append(event: E) {
        drainQueue()

        if (isKafkaProducerLog(event)) {
            // KafkaProducer가 발생시킨 로그라면 바로 보내지 않고, queue에 담았다가 다음 로그가 발생할 때 drainQueue에서 모아서 보낸다.
            offerToQueue(event)
        } else {
            super.doAppend(event)
        }
    }

    override fun doAppend(eventObject: E) {
        super.doAppend(eventObject)
    }

    override fun start() {
        // only error free appenders should be activated
        if (!options.checkOptions()) {
            return
        }

        check(producer != null) { "Kafka Producer is not created" }
        super.start()
    }

    override fun addAppender(newAppender: Appender<E>?) {
        TODO("Not yet implemented")
    }

    override fun iteratorForAppenders(): MutableIterator<Appender<E>> {
        TODO("Not yet implemented")
    }

    override fun getAppender(name: String?): Appender<E> {
        TODO("Not yet implemented")
    }

    override fun detachAndStopAllAppenders() {
        TODO("Not yet implemented")
    }

    override fun detachAppender(name: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun detachAppender(appender: Appender<E>?): Boolean {
        TODO("Not yet implemented")
    }

    override fun isAttached(appender: Appender<E>?): Boolean {
        TODO("Not yet implemented")
    }

    private fun createProducer(): KafkaProducer<ByteArray?, ByteArray> {
        return KafkaProducer(options.producerConfig)
    }

    private fun isKafkaProducerLog(event: E): Boolean {
        return event is ILoggingEvent && event.loggerName.startsWith(KAFKA_LOGGER_PREFIX)
    }

    private fun drainQueue() {
        var event = queue.poll()
        while (event != null) {
            doAppend(event)
        }
    }

    private fun offerToQueue(event: E) {
        queue.offer(event)
    }

}
