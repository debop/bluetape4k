package org.springframework.kafka.core

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.spring.messaging.support.message
import io.bluetape4k.support.uninitialized
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldHaveSize
import org.amshove.kluent.shouldNotBeEmpty
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.common.TopicPartition
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.aop.framework.ProxyFactory
import org.springframework.kafka.core.KafkaTemplateTests.Companion.INT_KEY_TOPIC
import org.springframework.kafka.core.KafkaTemplateTests.Companion.STRING_KEY_TOPIC
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.kafka.support.TopicPartitionOffset
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.condition.EmbeddedKafkaCondition
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import java.util.*
import java.util.concurrent.atomic.AtomicReference

@EmbeddedKafka(topics = [INT_KEY_TOPIC, STRING_KEY_TOPIC])
class KafkaTemplateTests {

    companion object: KLogging() {
        const val INT_KEY_TOPIC = "intKeyTopic"
        const val STRING_KEY_TOPIC = "stringKeyTopic"
    }

    private var embeddedKafka: EmbeddedKafkaBroker = uninitialized()
    private var consumer: Consumer<Int, String> = uninitialized()

    private val noopListener: ProducerFactory.Listener<String, String> =
        object: ProducerFactory.Listener<String, String> {
            override fun producerAdded(id: String, producer: Producer<String, String>) {}
            override fun producerRemoved(id: String, producer: Producer<String, String>) {}
        }

    private val noopProducerPostProcessor =
        ProducerPostProcessor { processor: Producer<String, String> -> processor }

    @BeforeAll
    fun setUp() {
        embeddedKafka = EmbeddedKafkaCondition.getBroker()
        val consumerProps = KafkaTestUtils
            .consumerProps("KafkaTemplatetests" + UUID.randomUUID(), "false", embeddedKafka)
        val cf = DefaultKafkaConsumerFactory<Int, String>(consumerProps)
        consumer = cf.createConsumer()
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, INT_KEY_TOPIC)
    }

    @AfterAll
    fun tearDown() {
        consumer.close()
    }

    @Test
    fun `send message`() {
        val senderProps = KafkaTestUtils.producerProps(embeddedKafka)
        val pf = DefaultKafkaProducerFactory<Int, String>(senderProps)
        val wrapper = AtomicReference<Producer<Int, String>>(null)
        pf.addPostProcessor { producer ->
            val prox = ProxyFactory()
            prox.setTarget(producer)
            @Suppress("UNCHECKED_CAST")
            val proxy = prox.proxy as Producer<Int, String>
            wrapper.set(proxy)
            proxy
        }
        val template = KafkaTemplate<Int, String>(pf, true)
        template.defaultTopic = INT_KEY_TOPIC
        template.setConsumerFactory(
            DefaultKafkaConsumerFactory(
                KafkaTestUtils.consumerProps("xx", "false", embeddedKafka)
            )
        )

        val initialRecords = template.receive(listOf(TopicPartitionOffset(INT_KEY_TOPIC, 1, 1L)))
        initialRecords.shouldBeEmpty()

        template.sendDefault("foo")
        KafkaTestUtils.getSingleRecord(consumer, INT_KEY_TOPIC).value() shouldBeEqualTo "foo"

        template.sendDefault(0, 2, "bar")
        var received: ConsumerRecord<Int, String> = KafkaTestUtils.getSingleRecord(consumer, INT_KEY_TOPIC).apply {
            partition() shouldBeEqualTo 0
            key() shouldBeEqualTo 2
            value() shouldBeEqualTo "bar"
        }

        template.sendDefault(1, 3, "qux")
        KafkaTestUtils.getSingleRecord(consumer, INT_KEY_TOPIC).apply {
            partition() shouldBeEqualTo 1
            key() shouldBeEqualTo 3
            value() shouldBeEqualTo "qux"
        }

        val message = message("fiz") {
            setHeader(KafkaHeaders.TOPIC, INT_KEY_TOPIC)
            setHeader(KafkaHeaders.PARTITION, 0)
            setHeader(KafkaHeaders.KEY, 2)
        }
        template.send(message)
        KafkaTestUtils.getSingleRecord(consumer, INT_KEY_TOPIC).apply {
            partition() shouldBeEqualTo 0
            key() shouldBeEqualTo 2
            value() shouldBeEqualTo "fiz"
        }

        val message2 = message("buz") {
            setHeader(KafkaHeaders.PARTITION, 1)
            setHeader(KafkaHeaders.KEY, 2)
        }
        template.send(message2)
        received = KafkaTestUtils.getSingleRecord(consumer, INT_KEY_TOPIC).apply {
            partition() shouldBeEqualTo 1
            key() shouldBeEqualTo 2
            value() shouldBeEqualTo "buz"
        }

        val metrics = template.execute { it.metrics() }
        metrics.shouldNotBeEmpty()
        val metrics2 = template.metrics()
        metrics2.shouldNotBeEmpty()

        val partitions = template.partitionsFor(INT_KEY_TOPIC)
        partitions.shouldNotBeEmpty() shouldHaveSize 2
        KafkaTestUtils.getPropertyValue(pf.createProducer(), "delegate") shouldBeEqualTo wrapper.get()

        // 마지막에 수신한 buz
        val receive = template.receive(INT_KEY_TOPIC, 1, received.offset())!!
        with(receive) {
            partition() shouldBeEqualTo 1
            key() shouldBeEqualTo 2
            value() shouldBeEqualTo "buz"
        }

        val records = template.receive(
            listOf(
                TopicPartitionOffset(INT_KEY_TOPIC, 1, 1L),
                TopicPartitionOffset(INT_KEY_TOPIC, 0, 1L),
                TopicPartitionOffset(INT_KEY_TOPIC, 0, 0L),
                TopicPartitionOffset(INT_KEY_TOPIC, 1, 0L),
            )
        )
        records.count() shouldBeEqualTo 4
        val partition2 = records.partitions()
        partition2 shouldContainSame setOf(TopicPartition(INT_KEY_TOPIC, 1), TopicPartition(INT_KEY_TOPIC, 0))

        records.records(TopicPartition(INT_KEY_TOPIC, 1)).map { it.offset() } shouldContainSame listOf(0L, 1L)
        records.records(TopicPartition(INT_KEY_TOPIC, 0)).map { it.offset() } shouldContainSame listOf(0L, 1L)

        pf.destroy()
    }

    @Test
    fun `send message with timestamp`() {
        val senderProps = KafkaTestUtils.producerProps(embeddedKafka)
        val pf = DefaultKafkaProducerFactory<Int, String>(senderProps)
        val template = KafkaTemplate(pf, true).apply {
            defaultTopic = INT_KEY_TOPIC
        }

        template.sendDefault(0, 1487694048607L, 0, "foo-ts1")
        KafkaTestUtils.getSingleRecord(consumer, INT_KEY_TOPIC).apply {
            partition() shouldBeEqualTo 0
            timestamp() shouldBeEqualTo 1487694048607L
            value() shouldBeEqualTo "foo-ts1"
        }

        template.send(INT_KEY_TOPIC, 1, 1487694048610L, 0, "foo-ts2")
        KafkaTestUtils.getSingleRecord(consumer, INT_KEY_TOPIC).apply {
            partition() shouldBeEqualTo 1
            timestamp() shouldBeEqualTo 1487694048610L
            value() shouldBeEqualTo "foo-ts2"
        }

        val metrics = template.execute { it.metrics() }
        metrics.shouldNotBeEmpty()

        val metrics2 = template.metrics()
        metrics2.shouldNotBeEmpty()

        val partitions = template.partitionsFor(INT_KEY_TOPIC)
        partitions.shouldNotBeEmpty() shouldHaveSize 2
        partitions.forEach {
            log.debug { "partition=$it" }
        }

        pf.destroy()
    }
}
