package io.bluetape4k.testcontainers.storage

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.trace
import io.bluetape4k.testcontainers.GenericServer
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import io.lettuce.core.RedisURI
import io.lettuce.core.cluster.RedisClusterClient
import io.lettuce.core.resource.ClientResources
import io.lettuce.core.resource.SocketAddressResolver
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.codec.LZ4Codec
import org.redisson.config.Config
import org.redisson.config.ReadMode
import org.redisson.config.SubscriptionMode
import org.testcontainers.DockerClientFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import java.net.SocketAddress
import java.util.concurrent.ConcurrentHashMap

class RedisClusterServer private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean,
    reuse: Boolean
): GenericContainer<RedisClusterServer>(imageName), GenericServer {

    companion object: KLogging() {
        const val IMAGE = "grokzen/redis-cluster"
        const val TAG = "6.2.1"

        //        const val IMAGE = "tommy351/redis-cluster"
//        const val TAG = "6.2"
        const val NAME = "redis.cluster"

        val PORTS = intArrayOf(7000, 7001, 7002, 7003, 7004, 7005)

        val DEFAULT_IMAGE_NAME: DockerImageName = DockerImageName.parse(IMAGE).withTag(TAG)

        @JvmStatic
        operator fun invoke(
            imageName: DockerImageName = DEFAULT_IMAGE_NAME,
            useDefaultPort: Boolean = false,
            reuse: Boolean = false,
        ): RedisClusterServer {
            return RedisClusterServer(imageName, useDefaultPort, reuse)
        }

        @JvmStatic
        operator fun invoke(
            tag: String,
            useDefaultPort: Boolean = false,
            reuse: Boolean = false,
        ): RedisClusterServer {
            val imageName = DockerImageName.parse(IMAGE).withTag(tag)
            return RedisClusterServer(imageName, useDefaultPort, reuse)
        }
    }

    override val port: Int get() = getMappedPort(PORTS[0])
    override val url: String get() = "redis://$host:$port"

    val mappedPorts: Map<Int, Int> by lazy { PORTS.associateWith { getMappedPort(it) } }
    val nodeAddresses: List<String> by lazy { mappedPorts.values.map { "redis://localhost:$it" } }
    private val socketAddresses = ConcurrentHashMap<Int, SocketAddress>()

    init {
        addExposedPorts(*PORTS)
        withReuse(reuse)
        withLogConsumer(Slf4jLogConsumer(log))
        setWaitStrategy(Wait.forListeningPort())

        addEnv("IP", "0.0.0.0")

        if (useDefaultPort) {
            exposeCustomPorts(*PORTS)
        }
    }

    override fun start() {
        super.start()

        val extraMaps = mapOf(
            "nodes" to nodeAddresses.joinToString(",")
        )
        writeToSystemProperties(NAME, extraMaps)
    }

    object Launcher {

        val redisCluster: RedisClusterServer by lazy {
            RedisClusterServer().apply {
                start()
                ShutdownQueue.register(this)
            }
        }

        object RedissonLib {
            fun getRedissonConfig(redisCluster: RedisClusterServer): Config {
                return Config().apply {
                    useClusterServers()
                        .setScanInterval(2000)
                        .setReadMode(ReadMode.SLAVE)
                        .setSubscriptionMode(SubscriptionMode.SLAVE)
                        .setNatMapper { redisURI ->
                            val port = redisCluster.mappedPorts[redisURI.port]!!
                            org.redisson.misc.RedisURI("redis", "localhost", port)
                        }
                        .apply {
                            nodeAddresses = redisCluster.nodeAddresses
                        }

                    this.setCodec(LZ4Codec())
                }
            }

            fun getRedisson(redisCluster: RedisClusterServer): RedissonClient {
                val config = getRedissonConfig(redisCluster)
                return Redisson.create(config).apply {
                    ShutdownQueue.register { this.shutdown() }
                }
            }
        }

        object LettuceLib {

            fun clientResources(redisCluster: RedisClusterServer): ClientResources {
                log.trace { "Get ClientResources..." }
                val socketAddressResolver = object: SocketAddressResolver() {
                    override fun resolve(redisURI: RedisURI): SocketAddress {
                        log.debug { "Resolve redisURI=$redisURI" }

                        val mappedPort = redisCluster.mappedPorts[redisURI.port]
                        log.debug { "redisURI.port=${redisURI.port}, mappedPort=$mappedPort" }

                        if (mappedPort != null) {
                            val socketAddress = redisCluster.socketAddresses[mappedPort]
                            if (socketAddress != null) {
                                log.trace { "mappedPort=$mappedPort, RedisCluster socketAddress=$socketAddress" }
                                return socketAddress
                            }
                            redisURI.port = mappedPort
                        }

                        redisURI.host = DockerClientFactory.instance().dockerHostIpAddress()
                        val socketAddress = super.resolve(redisURI)
                        redisCluster.socketAddresses.putIfAbsent(redisURI.port, socketAddress)
                        log.trace { "RedisCluster socketAddress=$socketAddress" }
                        return socketAddress
                    }
                }

                return ClientResources.builder()
                    .socketAddressResolver(socketAddressResolver)
                    .build().apply {
                        ShutdownQueue.register { this.shutdown() }
                    }
            }

            fun getClusterClient(redisCluster: RedisClusterServer): RedisClusterClient {
                val resources = clientResources(redisCluster)
                val uris = redisCluster.nodeAddresses.map { RedisURI.create(it) }
                return RedisClusterClient.create(resources, uris).apply {
                    ShutdownQueue.register(this)
                }
            }
        }
    }
}
