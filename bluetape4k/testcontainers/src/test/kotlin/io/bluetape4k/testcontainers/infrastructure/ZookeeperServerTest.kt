package io.bluetape4k.testcontainers.infrastructure

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.toUtf8Bytes
import io.bluetape4k.support.toUtf8String
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.SAME_THREAD)
class ZookeeperServerTest {

    companion object: KLogging()

    @Nested
    inner class UseDefaultPort {
        @Test
        fun `create zookeeper with default port`() {
            ZookeeperServer(useDefaultPort = true).use { zks ->
                zks.start()
                zks.isRunning.shouldBeTrue()
                zks.port shouldBeEqualTo ZookeeperServer.PORT

                verifyZookeeper(zks)
            }
        }
    }

    @Nested
    inner class UseDockerPort {
        @Test
        fun `create zookeeper with docker port`() {
            ZookeeperServer.Launcher.zookeeper.use { zks ->
                zks.isRunning.shouldBeTrue()
                verifyZookeeper(zks)
            }

        }
    }

    private fun verifyZookeeper(zks: ZookeeperServer) {
        val path = "/messages/zk-tc"
        val content = "Running Zookeeper with Testcontainers"
        withCuratorFramework(zks) {
            create().creatingParentsIfNeeded().forPath(path, content.toUtf8Bytes())

            val retrieved = data.forPath(path).toUtf8String()
            retrieved shouldBeEqualTo content
        }
    }
}
