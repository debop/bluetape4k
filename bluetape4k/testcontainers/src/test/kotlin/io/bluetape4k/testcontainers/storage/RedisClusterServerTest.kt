package io.bluetape4k.testcontainers.storage

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.info
import io.lettuce.core.cluster.ClusterClientOptions
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions
import io.lettuce.core.cluster.SlotHash
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.redisson.api.redisnode.RedisNodes
import java.time.Duration


@Execution(ExecutionMode.SAME_THREAD)
class RedisClusterServerTest {

    companion object: KLogging()

    @Test
    fun `create redis cluster server`() {
        RedisClusterServer().use { redisCluster ->
            redisCluster.start()
            redisCluster.isRunning.shouldBeTrue()

            verifyWithLettuce(redisCluster)
            verifyWithRedisson(redisCluster)

        }
    }

    //    @Disabled("Mac AirPlay 사용 시 Port 5000, 7000를 사용해서 충돌이 생깁니다")
    @Test
    fun `create redis server with default port`() {
        RedisClusterServer(useDefaultPort = true).use { redisCluster ->
            redisCluster.start()
            redisCluster.isRunning.shouldBeTrue()
            redisCluster.port shouldBeEqualTo RedisClusterServer.PORTS[0]

            verifyWithLettuce(redisCluster)
            verifyWithRedisson(redisCluster)
        }
    }

    private fun verifyWithLettuce(redisCluster: RedisClusterServer) {
        RedisClusterServer.Launcher.LettuceLib.getClusterClient(redisCluster).use { clusterClient ->
            val topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .enablePeriodicRefresh(Duration.ofSeconds(30))
                .enableAllAdaptiveRefreshTriggers()
                .build()
            val clusterClientOptions = ClusterClientOptions.builder()
                .topologyRefreshOptions(topologyRefreshOptions)
                .autoReconnect(true)
                .build()

            clusterClient.setOptions(clusterClientOptions)

            log.debug { "Connect to Redis Cluster. clusterClient: $clusterClient" }

            // TODO: Redis Cluster 구성에 시간이 걸리므로, 이렇게 내부검사를 통해 확인한 후에 사용이 가능하도록 해주어야 한다
            // TODO: 예: RedisClusterServer.isClusterStarted() 같은 것으로 제공해야 한다
            await until {
                var clusterStarted = false
                clusterClient.connect().use { connection ->
                    val commands = connection.sync()

                    val clusterInfo: String = commands.clusterInfo()
                    // log.info { "cluster info: $clusterInfo" }
                    if (clusterInfo.contains("cluster_state:ok")) {
                        val assignedPartition = clusterClient.partitions.sumOf { it.slots.size }
                        if (assignedPartition == SlotHash.SLOT_COUNT) {
                            // fake get for checking cluster
                            runCatching {
                                commands.get("42")
                            }.onSuccess {
                                clusterStarted = true
                            }
                        } else {
                            clusterClient.refreshPartitions()
                        }
                    }
                }
                clusterStarted
            }

            clusterClient.connect().use { connection ->
                val commands = connection.sync()
                log.info { "cluster info: ${commands.clusterInfo()}" }

                commands.ping() shouldBeEqualTo "PONG"
                // 모든 노드에 PING 을 실행해봅니다.
                commands.all().commands().ping().forEach { it shouldBeEqualTo "PONG" }
                commands.set("key", "value")
                commands.get("key") shouldBeEqualTo "value"
            }
        }
    }

    private fun verifyWithRedisson(redisCluster: RedisClusterServer) {
        val redisson = RedisClusterServer.Launcher.RedissonLib.getRedisson(redisCluster)
        try {
            redisson.getRedisNodes(RedisNodes.CLUSTER).masters.all { it.ping() }.shouldBeTrue()
            redisson.getRedisNodes(RedisNodes.CLUSTER).pingAll().shouldBeTrue()

            val map = redisson.getMap<String, String>("bluetape4k:map")
            map["key"] = "value"
            map["key"] shouldBeEqualTo "value"
        } finally {
            redisson.shutdown()
        }
    }
}
