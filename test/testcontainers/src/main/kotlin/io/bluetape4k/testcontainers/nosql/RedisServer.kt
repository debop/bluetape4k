package io.bluetape4k.testcontainers.nosql

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.GenericServer
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.api.sync.RedisCommands
import io.lettuce.core.codec.RedisCodec
import java.util.concurrent.ConcurrentHashMap
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName


/**
 * Docker를 이용하여 Redis Server를 실행합니다.
 *
 * ```
 * val redisServer = RedisServer().apply {
 *      start()
 *      ShutdownQueue.register(this)
 * }
 * ```
 *
 * 참고: [Redis Docker image](https://hub.docker.com/_/redis)
 */
class RedisServer private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean,
    reuse: Boolean,
): GenericContainer<RedisServer>(imageName), GenericServer {

    companion object: KLogging() {
        const val IMAGE_NAME = "redis"
        const val DEFAULT_TAG = "6.2"

        const val REDIS_NAME = "redis"
        const val REDIS_PORT = 6379

        operator fun invoke(
            tag: String = DEFAULT_TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): RedisServer {
            require(tag.isNotBlank()) { "tag must not be blank." }
            return RedisServer(DockerImageName.parse("$IMAGE_NAME:$tag"), useDefaultPort, reuse)
        }

        operator fun invoke(
            imageName: DockerImageName,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): RedisServer {
            return RedisServer(imageName, useDefaultPort, reuse)
        }
    }

    override val url: String
        get() = "$REDIS_NAME://$host:$port"

    init {
        withExposedPorts(REDIS_PORT)

        withReuse(reuse)
        withLogConsumer(Slf4jLogConsumer(log))
        setWaitStrategy(Wait.forListeningPort())

        if (useDefaultPort) {
            exposeCustomPorts(REDIS_PORT)
        }
    }

    override fun start() {
        super.start()
        writeToSystemProperties(REDIS_NAME)
    }

    object Launcher {

        val redis: RedisServer by lazy {
            RedisServer().apply {
                start()
                ShutdownQueue.register(this)
            }
        }

        object RedissonLib {
            // private val redissonClients = ConcurrentHashMap<String, RedissonClient>()

            fun getRedissonConfig(address: String = redis.url): Config =
                Config().apply {
                    useSingleServer().address = address
                }

            fun getRedisson(address: String = redis.url): RedissonClient {
                return Redisson.create(getRedissonConfig(address))
                    .also { redisson ->
                        ShutdownQueue.register { redisson.shutdown() }
                    }
            }
        }

        object LettuceLib {

            private val redisClients = ConcurrentHashMap<String, RedisClient>()

            fun getRedisURI(host: String, port: Int): RedisURI =
                RedisURI.Builder.redis(host, port).build()

            fun getRedisClient(host: String = redis.host, port: Int = redis.port): RedisClient =
                redisClients.computeIfAbsent("$host:$port") {
                    RedisClient.create(getRedisURI(host, port))
                        .also { redisClient ->
                            ShutdownQueue.register { redisClient.shutdown() }
                        }
                }

            fun getRedisCommands(
                host: String = redis.host,
                port: Int = redis.port,
            ): RedisCommands<String, String?> = getRedisClient(host, port).connect().sync()

            fun getRedisAsyncCommands(
                host: String = redis.host,
                port: Int = redis.port,
            ): RedisAsyncCommands<String, String?> = getRedisClient(host, port).connect().async()


            fun <K: Any, V> getRedisCommands(
                host: String = redis.host,
                port: Int = redis.port,
                codec: RedisCodec<K, V>,
            ): RedisCommands<K, V> = getRedisClient(host, port).connect(codec).sync()


            fun <K: Any, V> getRedisAsyncCommands(
                host: String = redis.host,
                port: Int = redis.port,
                codec: RedisCodec<K, V>,
            ): RedisAsyncCommands<K, V> = getRedisClient(host, port).connect(codec).async()
        }
    }
}
