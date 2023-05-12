package io.bluetape4k.testcontainers.storage

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
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import java.util.concurrent.ConcurrentHashMap


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
        const val IMAGE = "redis"
        const val TAG = "7"
        const val NAME = "redis"
        const val PORT = 6379

        @JvmStatic
        operator fun invoke(
            tag: String = TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): RedisServer {
            require(tag.isNotBlank()) { "tag must not be blank." }
            val imageName = DockerImageName.parse(IMAGE).withTag(tag)
            return RedisServer(imageName, useDefaultPort, reuse)
        }

        @JvmStatic
        operator fun invoke(
            imageName: DockerImageName,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): RedisServer {
            return RedisServer(imageName, useDefaultPort, reuse)
        }
    }

    override val port: Int get() = getMappedPort(PORT)
    override val url: String get() = "$NAME://$host:$port"

    init {
        addExposedPorts(PORT)
        withReuse(reuse)

        if (useDefaultPort) {
            exposeCustomPorts(PORT)
        }
    }

    override fun start() {
        super.start()
        writeToSystemProperties(NAME)
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
