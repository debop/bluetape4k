package io.bluetape4k.testcontainers.nosql

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.SAME_THREAD)
class HazelcastServerTest {

    companion object: KLogging()

    @Test
    fun `create hazelcast server`() {
        val hazelcast = HazelcastServer.Launcher.hazelcast

        assertHazelcast(hazelcast)
    }

    @Test
    fun `create hazelcast server with default port`() {
        HazelcastServer(useDefaultPort = true)
            .withRESTClient()
            .withHttpHealthCheck()
            .use {
                it.start()
                assertHazelcast(it)
                it.port shouldBeEqualTo HazelcastServer.PORT
            }
    }

    private fun assertHazelcast(hazelcast: HazelcastServer) {
        hazelcast.isRunning.shouldBeTrue()
    }
}
