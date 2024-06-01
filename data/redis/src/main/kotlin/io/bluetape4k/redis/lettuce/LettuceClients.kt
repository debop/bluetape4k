package io.bluetape4k.redis.lettuce

import io.bluetape4k.logging.KLogging
import io.bluetape4k.redis.RedisConst
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.api.sync.RedisCommands
import io.lettuce.core.codec.RedisCodec
import io.lettuce.core.resource.ClientResources
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

/**
 * Lettuce 의 [RedisClient] 등을 생성해주는 유틸리티 클래스입니다.
 */
object LettuceClients: KLogging() {

    @JvmField
    val DEFAULT_REDIS_URI: RedisURI = LettuceClients.getRedisURI()

    fun getRedisURI(
        host: String = RedisConst.DEFAULT_HOST,
        port: Int = RedisConst.DEFAULT_PORT,
        timeoutInMillis: Long = RedisConst.DEFAULT_TIMEOUT_MILLIS,
    ): RedisURI {
        return RedisURI.builder()
            .withHost(host)
            .withPort(port)
            .withTimeout(timeoutInMillis.milliseconds.toJavaDuration())
            .build()
    }

    /**
     * [RedisClient] 인스턴스를 생성합니다.
     *
     * @param url Redis Server URL (e.g. redis://localhost:6379)
     * @return [RedisClient] instance
     */
    fun clientOf(url: String): RedisClient = clientOf(RedisURI.create(url))

    /**
     * [RedisClient] 인스턴스를 생성합니다.
     *
     * @param redisUri Redis Server URI
     * @return [RedisClient] instance
     */
    fun clientOf(redisUri: RedisURI): RedisClient = RedisClient.create(redisUri)

    /**
     * [RedisClient] 인스턴스를 생성합니다.
     *
     * @param redisUri Redis Server URI
     * @return [RedisClient] instance
     */
    fun clientOf(clientResources: ClientResources): RedisClient = RedisClient.create(clientResources)

    /**
     * [RedisClient] 인스턴스를 생성합니다.
     *
     * @param host  redis server host
     * @param port  redis server port
     * @param timeoutInMillis connectim timeout in milliseconds
     * @return [RedisClient] instance
     */
    fun clientOf(
        host: String = RedisConst.DEFAULT_HOST,
        port: Int = RedisConst.DEFAULT_PORT,
        timeoutInMillis: Long = RedisConst.DEFAULT_TIMEOUT_MILLIS,
    ): RedisClient =
        LettuceClients.clientOf(
            LettuceClients.getRedisURI(
                host,
                port,
                timeoutInMillis
            )
        )

    fun connect(client: RedisClient): StatefulRedisConnection<String, String> =
        client.connect()

    fun <V: Any> connect(client: RedisClient, codec: RedisCodec<String, V>): StatefulRedisConnection<String, V> =
        client.connect(codec)

    fun commands(client: RedisClient): RedisCommands<String, String> =
        LettuceClients.connect(client).sync()

    fun <V: Any> commands(client: RedisClient, codec: RedisCodec<String, V>): RedisCommands<String, V> =
        LettuceClients.connect(client, codec).sync()

    fun asyncCommands(client: RedisClient): RedisAsyncCommands<String, String> =
        LettuceClients.connect(client).async()

    fun <V: Any> asyncCommands(client: RedisClient, codec: RedisCodec<String, V>): RedisAsyncCommands<String, V> =
        LettuceClients.connect(client, codec).async()
}
