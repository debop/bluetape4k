package io.bluetape4k.quarkus.tests.containers

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import io.bluetape4k.testcontainers.massage.KafkaServer
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager

class KafkaTestResource: QuarkusTestResourceLifecycleManager {

    companion object: KLogging() {
        val kafka by lazy { KafkaServer.Launcher.kafka }
        val bootstrapServers: String get() = kafka.bootstrapServers
    }

    override fun start(): MutableMap<String, String> {
        log.info { "Start Kafka test resource ..." }
        kafka.start()
        return mutableMapOf(
            "quarkus.kafka-streams.bootstrap-servers" to kafka.bootstrapServers,
        )
    }

    override fun stop() {
        log.info { "Stop Kafka test resource ..." }
        runCatching { kafka.stop() }
    }
}
