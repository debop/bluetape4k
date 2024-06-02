package io.bluetape4k.workshop.kafka.ping

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.massage.KafkaServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.listener.GenericMessageListenerContainer
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate

@SpringBootApplication
class PingApplication {

    companion object: KLogging() {
        private val kafka = KafkaServer.Launcher.kafka
    }

    /**
     * Replying kafka template - Producing 후에 응답을 Consuming 합니다.
     *
     * @param producerFactory   Kafka Producer 를 생성해주는 Factory
     * @param listenerContainer Consumer 를 생성해주는 [GenericMessageListenerContainer]
     */
    @Bean
    fun replyingKafkaTemplate(
        producerFactory: ProducerFactory<String, String>,
        listenerContainer: GenericMessageListenerContainer<String, String>,
    ): ReplyingKafkaTemplate<String, String, String> {
        return ReplyingKafkaTemplate(producerFactory, listenerContainer)
    }

    /**
     * 응답받을 Message Listener를 제공하는 [ConcurrentMessageListenerContainer]
     *
     * @param containerFactory Kafka Listener Container's Factory
     */
    @Bean
    fun listenerContainer(
        containerFactory: ConcurrentKafkaListenerContainerFactory<String, String>,
    ): ConcurrentMessageListenerContainer<String, String> {
        return containerFactory.createContainer("replies").apply {
            containerProperties.setGroupId("repliesGroup")
            isAutoStartup = false
        }
    }
}

fun main(vararg args: String) {
    runApplication<PingApplication>(*args)
}
