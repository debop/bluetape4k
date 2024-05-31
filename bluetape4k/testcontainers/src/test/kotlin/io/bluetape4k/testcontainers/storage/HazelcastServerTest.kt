package io.bluetape4k.testcontainers.storage

import com.hazelcast.client.HazelcastClient
import com.hazelcast.client.config.ClientConfig
import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.ShutdownQueue
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.lifecycle.Startables

@Execution(ExecutionMode.SAME_THREAD)
class HazelcastServerTest {

    companion object: KLogging() {
        private const val TEST_QUEUE_NAME = "test-queue"
        private const val TEST_VALUE = "Hello!"
        private const val FALSE_VALUE = "false"
        private const val TRUE_VALUE = "true"

        private const val HZ_CLUSTERNAME_ENV_NAME = "HZ_CLUSTERNAME"
        private const val HZ_NETWORK_JOIN_AZURE_ENABLED_ENV_NAME = "HZ_NETWORK_JOIN_AZURE_ENABLED"
        private const val HZ_NETWORK_JOIN_MULTICAST_ENABLED_ENV_NAME = "HZ_NETWORK_JOIN_MULTICAST_ENABLED"
        private const val CLUSTER_STARTUP_LOG_MESSAGE_REGEX = ".*Members \\{size:2.*"
        private const val TEST_CLUSTER_NAME = "test-cluster"
    }

    @Test
    fun `create hazelcast server`() {
        HazelcastServer()
            .withRESTClient()
            .withHttpHealthCheck().use {
                it.start()
                it.isRunning.shouldBeTrue()
                assertHazelcastQueue(it)
            }
    }

    @Test
    fun `create hazelcast server with default port`() {
        HazelcastServer(useDefaultPort = true)
            .withRESTClient()
            .withHttpHealthCheck()
            .use {
                it.start()
                it.isRunning.shouldBeTrue()
                it.port shouldBeEqualTo HazelcastServer.PORT
                assertHazelcastQueue(it)
            }
    }

    private fun assertHazelcastQueue(hazelcast: HazelcastServer) {
        val clientConfig = ClientConfig().apply {
            networkConfig.addAddress(hazelcast.url)
        }
        val client = HazelcastClient.newHazelcastClient(clientConfig)
        try {
            val queue = client.getQueue<String>(TEST_QUEUE_NAME)
            queue.put(TEST_VALUE)
            queue.take() shouldBeEqualTo TEST_VALUE

        } finally {
            client.shutdown()
        }
    }

    @Test
    fun `create hazelcast cluster`() {
        val network = Network.newNetwork()

        val hazelcast1 = HazelcastServer()
            .withEnv(HZ_CLUSTERNAME_ENV_NAME, TEST_CLUSTER_NAME)
            .withEnv(HZ_NETWORK_JOIN_AZURE_ENABLED_ENV_NAME, FALSE_VALUE)
            .withEnv(HZ_NETWORK_JOIN_MULTICAST_ENABLED_ENV_NAME, TRUE_VALUE)
            .waitingFor(Wait.forLogMessage(CLUSTER_STARTUP_LOG_MESSAGE_REGEX, 1))
            .withNetwork(network)

        val hazelcast2 = HazelcastServer()
            .withEnv(HZ_CLUSTERNAME_ENV_NAME, TEST_CLUSTER_NAME)
            .withEnv(HZ_NETWORK_JOIN_AZURE_ENABLED_ENV_NAME, FALSE_VALUE)
            .withEnv(HZ_NETWORK_JOIN_MULTICAST_ENABLED_ENV_NAME, TRUE_VALUE)
            .waitingFor(Wait.forLogMessage(CLUSTER_STARTUP_LOG_MESSAGE_REGEX, 1))
            .withNetwork(network)

        ShutdownQueue.register(hazelcast1)
        ShutdownQueue.register(hazelcast2)

        Startables.deepStart(hazelcast1, hazelcast2).join()

        hazelcast1.isRunning.shouldBeTrue()
        hazelcast2.isRunning.shouldBeTrue()

        val clientConfig = ClientConfig().apply {
            setClusterName(TEST_CLUSTER_NAME)
                .networkConfig
                .addAddress(hazelcast1.url)
                .addAddress(hazelcast2.url)
        }

        val client = HazelcastClient.newHazelcastClient(clientConfig)
        try {
            client.cluster.members shouldHaveSize 2

            val queue = client.getQueue<String>(TEST_QUEUE_NAME)
            queue.put(TEST_VALUE)

            queue.take() shouldBeEqualTo TEST_VALUE
        } finally {
            client.shutdown()
        }
    }
}
