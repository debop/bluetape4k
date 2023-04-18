package io.bluetape4k.testcontainers.massage

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.GenericServer
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import java.util.UUID
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

/**
 * Docker를 이용하여 [kafka](http://kafka.apache.org)를 구동해주는 container 입니다.
 *
 * 참고: [Kafka official images](https://hub.docker.com/_/kafka?tab=description&page=1&ordering=last_updated)
 *
 * 비교: [kafka-junit](https://github.com/charithe/kafka-junit) 를 사용하면 Docker 없이도 가능합니다.
 *
 * ```
 * // start kafka server by docker
 * val kafka = KafkaServer().apply { start() }
 * ```
 */
class KafkaServer private constructor(
    imageName: DockerImageName,
    useTransaction: Boolean,
    useDefaultPort: Boolean,
    reuse: Boolean,
): KafkaContainer(imageName), GenericServer {

    companion object: KLogging() {
        val IMAGE = "confluentinc/cp-kafka"
        val NAME = "kafka"
        val TAG = "7.3.3"

        operator fun invoke(
            tag: String = TAG,
            useTransaction: Boolean = false,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): KafkaServer {
            val imageName = DockerImageName.parse(IMAGE).withTag(tag)
            return KafkaServer(imageName, useTransaction, useDefaultPort, reuse)
        }

        operator fun invoke(
            imageName: DockerImageName,
            useTransaction: Boolean = false,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): KafkaServer {
            return KafkaServer(imageName, useTransaction, useDefaultPort, reuse)
        }
    }

    init {
        withExposedPorts(KAFKA_PORT)
        withEmbeddedZookeeper()
        withReuse(reuse)
        withLogConsumer(Slf4jLogConsumer(log))
        setWaitStrategy(Wait.forListeningPort())

        // HINT: Transaction 관련 테스트를 위해서는 다음과 같은 값을 넣어줘야 합니다.
        // HINT: 테스트 시에는 transaction log replica 를 1로 설정해야 합니다. (기본은 3)
        // see : https://github.com/testcontainers/testcontainers-java/issues/1816
        if (useTransaction) {
            addEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1")
            addEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", "1")
        }

        if (useDefaultPort) {
            exposeCustomPorts(KAFKA_PORT)
        }
    }

    override fun start() {
        super.start()

        val extraProps = mapOf(
            "bootstrapServers" to bootstrapServers,
            "boundPortNumbers" to boundPortNumbers.joinToString()
        )
        writeToSystemProperties(NAME, extraProps)
    }

    object Launcher {
        val kafka: KafkaServer by lazy {
            KafkaServer().apply {
                start()
                ShutdownQueue.register(this)
            }
        }

        private val stringSerializer = StringSerializer()
        private val stringDeserializer = StringDeserializer()

        fun createStringProducer(kafkaServer: KafkaServer = kafka): KafkaProducer<String, String> {
            val map = mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaServer.bootstrapServers,
                ProducerConfig.CLIENT_ID_CONFIG to UUID.randomUUID().toString(),
                ProducerConfig.COMPRESSION_TYPE_CONFIG to "lz4",
                ProducerConfig.LINGER_MS_CONFIG to "0",
                ProducerConfig.BATCH_SIZE_CONFIG to "1"
            )
            return KafkaProducer(map, stringSerializer, stringSerializer)
        }

        fun createStringConsumer(kafkaServer: KafkaServer = kafka): KafkaConsumer<String, String> {
            val map = mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaServer.bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG to UUID.randomUUID().toString(),
                ConsumerConfig.CLIENT_ID_CONFIG to UUID.randomUUID().toString(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to "true",
                ConsumerConfig.RETRY_BACKOFF_MS_CONFIG to 100
            )
            return KafkaConsumer(map, stringDeserializer, stringDeserializer)
        }
    }
}
