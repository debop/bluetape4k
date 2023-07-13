package io.nats.examples.jetstream.simple

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.nats.client.api.consumerConfiguration
import io.bluetape4k.nats.client.createOrReplaceStream
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.concurrent.thread

class IterableConsumerExample: AbstractSimpleExample() {

    companion object: KLogging() {
        private const val STREAM = "manually-stream"
        private const val SUBJECT = "manually-subject"
        private const val CONSUMER_NAME = "manually-consumer"
        private const val MESSAGE_TEXT = "manually"
        private const val STOP_COUNT = 500
        private const val REPORT_EVERY = 50
        private const val JITTER = 20
    }

    @Test
    fun `use IterableConsumer`() = runTest {
        getConnection().use { nc ->
            val js = nc.jetStream()
            nc.createOrReplaceStream(STREAM, SUBJECT)

            // get stream context, create consumer and get the consumer context
            val streamContext = nc.streamContext(STREAM)
            val consumerContext = streamContext.createOrUpdateConsumer(consumerConfiguration { durable(CONSUMER_NAME) })

            val consumeThread = thread(start = false) {
                var count = 0
                val start = System.currentTimeMillis()

                consumerContext.consume().use { consumer ->
                    log.debug { "Starting consuming ..." }
                    while (count < STOP_COUNT) {
                        val msg = consumer.nextMessage(1000)
                        msg?.let {
                            it.ack()
                            if (++count % REPORT_EVERY == 0) {
                                report("Main Loop Running", System.currentTimeMillis() - start, count)
                            }
                        }
                    }
                    report("Main Loop Stopped", System.currentTimeMillis() - start, count)

                    log.debug { "Pausing for effect...allow more messages come across." }
                    Thread.sleep((JITTER * 2).toLong()) // allows more messages to come across
                    consumer.stop(1000)

                    println("Starting post-stop loop.")
                    var msg = consumer.nextMessage(1000)
                    while (msg != null) {
                        msg.ack()
                        report("Post-stop loop running", System.currentTimeMillis() - start, ++count)
                        msg = consumer.nextMessage(1000)
                    }
                }
            }
            consumeThread.start()

            val publisher = Publisher(js, SUBJECT, MESSAGE_TEXT, JITTER)
            val publishThread = Thread(publisher)
            publishThread.start()

            consumeThread.join()
            publisher.stopPublishing()
            publishThread.join()
        }
    }

    private fun report(label: String, ms: Long, count: Int) {
        log.debug { "$label: Received $count messages in ${ms}ms." }
    }
}
