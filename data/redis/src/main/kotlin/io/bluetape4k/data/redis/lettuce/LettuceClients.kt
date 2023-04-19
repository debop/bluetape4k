package io.bluetape4k.data.redis.lettuce

import io.bluetape4k.data.redis.RedisConst
import io.bluetape4k.logging.KLogging
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.api.sync.RedisCommands
import io.lettuce.core.codec.RedisCodec
import java.time.Duration

object LettuceClients: KLogging() {

    @JvmField
    val DEFAULT_REDIS_URI: RedisURI = getRedisURI()

    fun getRedisURI(
        host: String = RedisConst.DEFAULT_HOST,
        port: Int = RedisConst.DEFAULT_PORT,
        timeoutInMillis: Long = RedisConst.DEFAULT_TIMEOUT_MILLIS,
    ): RedisURI =
        RedisURI.Builder
            .redis(host, port)
            .withTimeout(Duration.ofMillis(timeoutInMillis))
            .build()

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
        clientOf(getRedisURI(host, port, timeoutInMillis))

    fun connect(client: RedisClient): StatefulRedisConnection<String, String> =
        client.connect()

    fun <V: Any> connect(client: RedisClient, codec: RedisCodec<String, V>): StatefulRedisConnection<String, V> =
        client.connect(codec)

    fun commands(client: RedisClient): RedisCommands<String, String> =
        connect(client).sync()

    fun <V: Any> commands(client: RedisClient, codec: RedisCodec<String, V>): RedisCommands<String, V> =
        connect(client, codec).sync()

    fun asyncCommands(client: RedisClient): RedisAsyncCommands<String, String> =
        connect(client).async()

    fun <V: Any> asyncCommands(client: RedisClient, codec: RedisCodec<String, V>): RedisAsyncCommands<String, V> =
        connect(client, codec).async()
}
