package io.bluetape4k.workshop.redis.examples

import io.bluetape4k.data.redis.spring.serializer.RedisBinarySerializers
import io.bluetape4k.data.redis.spring.serializer.redisSerializationContext
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.uninitialized
import io.bluetape4k.testcontainers.storage.RedisServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport
import java.util.concurrent.TimeUnit
import jakarta.annotation.PreDestroy

fun main(vararg args: String) {
    runApplication<RedisApplication>(*args)
}

@SpringBootApplication
class RedisApplication {

    companion object: KLogging() {
        @JvmStatic
        val redis = RedisServer.Launcher.redis
    }

    @Autowired
    private val factory: RedisConnectionFactory = uninitialized()

    @Autowired
    private val reactiveFactory: ReactiveRedisConnectionFactory = uninitialized()

    @Bean
    fun redisTemplate(factory: RedisConnectionFactory): RedisTemplate<*, *> {
        // RedisSerializer 를 압축이 가능한 BinarySerializer 를 사용합니다.
        return RedisTemplate<ByteArray, ByteArray>().apply {
            setConnectionFactory(factory)
            keySerializer = StringRedisSerializer.UTF_8
            valueSerializer = RedisBinarySerializers.LZ4Kryo
        }
    }

    @Bean
    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<*, *> {
        val context = redisSerializationContext<ByteArray, ByteArray>(RedisSerializer.byteArray()) {
            key(RedisSerializer.byteArray())
            hashKey(RedisSerializer.byteArray())
            value(RedisBinarySerializers.LZ4)
            hashValue(RedisBinarySerializers.LZ4)
            string(RedisSerializer.string())
        }
        return ReactiveRedisTemplate(factory, context)
    }

    @PreDestroy
    fun flushTestDb() {
        factory.connection.flushDb()
    }

    @Bean
    fun repoMetricsPostProcessor(): RepoMetricsPostProcessor {
        return RepoMetricsPostProcessor()
    }

    class RepoMetricsPostProcessor: BeanPostProcessor {
        companion object: KLogging()

        override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {

            if (bean is RepositoryFactoryBeanSupport<*, *, *>) {
                // RepositoryFactory가 Repository를 생성할 때 작업을 추가할 수 있습니다.
                bean.addRepositoryFactoryCustomizer { factory ->
                    /**
                     * [CrudRepository] 실행 정보를 Metrics로 측정할 수 있도록 할 수 있습니다.
                     */
                    factory.addInvocationListener { invocation ->
                        log.debug {
                            val method = invocation.repositoryInterface.simpleName + "." + invocation.method.name
                            val elapsed = invocation.getDuration(TimeUnit.MILLISECONDS)
                            val result = invocation.result?.state
                            "method=$method: $elapsed msec - $result"
                        }
                    }
                }
            }
            return bean
        }
    }
}
