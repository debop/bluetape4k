package org.springframework.kafka.core

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.spring.coroutines.await
import io.bluetape4k.support.uninitialized
import io.bluetape4k.testcontainers.massage.KafkaServer
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer

@SpringBootTest
class SimpleKafkaExamples {

    @Configuration
    @EnableKafka
    class TestConfiguration {
        @Bean
        fun producerFactory(): ProducerFactory<String, String> {
            return KafkaServer.Launcher.Spring.getStringProducerFactory()
        }

        @Bean
        fun consumerFactory(): ConsumerFactory<String, String> {
            return KafkaServer.Launcher.Spring.getStringConsumerFactory()
        }

        @Bean
        fun kafkaTemplate(
            producerFactory: ProducerFactory<String, String>,
            consumerFactory: ConsumerFactory<String, String>,
        ): KafkaTemplate<String, String> {
            return KafkaTemplate(producerFactory, true).apply {
                defaultTopic = STRING_TOPIC_NAME
                setConsumerFactory(consumerFactory)
            }
        }

        @Bean
        fun kafkaListenerConainerFactory(
            consumerFactory: ConsumerFactory<String, String>,
        ): ConcurrentKafkaListenerContainerFactory<String, String> {
            return KafkaServer.Launcher.Spring.getConcurrentKafkaListenerContainerFactory(consumerFactory)
        }

        @Bean
        fun kafkaManualAckListenerContainerFactory(
            consumerFactory: ConsumerFactory<String, String>,
        ): KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> {
            return KafkaServer.Launcher.Spring
                .getKafkaManualAckListenerContainerFactory(consumerFactory)
        }
    }

    companion object: KLogging() {
        const val STRING_TOPIC_NAME = "simple.kafka.string-topic.1"
    }

    @Autowired
    private val kafkaTemplate: KafkaTemplate<String, String> = uninitialized()

    @AfterEach
    fun afterEach() {
        Thread.sleep(100)
    }

    @Test
    fun `context loading`() {
        kafkaTemplate.shouldNotBeNull()
    }

    @Test
    fun `send string message`() = runTest {
        val key = "simple key"
        val message = "simple message"

        val result = kafkaTemplate.send(STRING_TOPIC_NAME, key, message).await()
        log.debug { "produceRecord=${result.producerRecord}" }
        log.debug { "recordMetadata=${result.recordMetadata}" }
        result.recordMetadata.hasTimestamp().shouldBeTrue()
        result.recordMetadata.hasOffset().shouldBeTrue()

        val result2 = kafkaTemplate.send(STRING_TOPIC_NAME, key, message).await()
        log.debug { "produceRecord=${result2.producerRecord}" }
        log.debug { "recordMetadata=${result2.recordMetadata}" }
        result2.recordMetadata.hasTimestamp().shouldBeTrue()
        result2.recordMetadata.hasOffset().shouldBeTrue()

        // 테스트용 Kafka 라서 partition 은 1만 갖도록 한다 
        result2.recordMetadata.partition() shouldBeEqualTo result.recordMetadata.partition()
        result2.recordMetadata.offset() shouldBeGreaterThan result.recordMetadata.offset()
    }
}
