package io.bluetape4k.infra.cache.nearcache.coroutines

import io.bluetape4k.infra.cache.jcache.coroutines.CoCache
import io.bluetape4k.infra.cache.jcache.coroutines.RedissonCoCache
import io.bluetape4k.infra.cache.jcache.jcacheConfiguration
import io.bluetape4k.junit5.awaitility.untilSuspending
import io.bluetape4k.junit5.output.CaptureOutput
import io.bluetape4k.junit5.output.OutputCapturer
import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.storage.RedisServer
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContain
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.TimeUnit
import javax.cache.expiry.CreatedExpiryPolicy
import javax.cache.expiry.Duration

@CaptureOutput
class RedissonNearCoCacheTest: AbstractNearCoCacheTest() {

    companion object: KLogging() {
        private val redis by lazy { RedisServer.Launcher.redis }

        private val redisson by lazy {
            RedisServer.Launcher.RedissonLib.getRedisson()
        }
    }

    override val backCoCache: CoCache<String, Any> by lazy {
        val configuration = jcacheConfiguration<String, Any> {
            setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration(TimeUnit.MILLISECONDS, 1000)))
        }
        RedissonCoCache("redis-back-cocache" + UUID.randomUUID().toString(), redisson, configuration)
    }

    @Test
    fun `back cache entry가 expire 되면 event listener를 통해 front cache가 삭제됩니다`(output: OutputCapturer) = runTest {
        val key = getKey()
        val value = getValue()

        nearCoCache1.put(key, value)
        await untilSuspending { nearCoCache2.containsKey(key) }

        nearCoCache1.containsKey(key).shouldBeTrue()
        nearCoCache2.containsKey(key).shouldBeTrue()

        // NOTE: backCache 에서 cache expire 가 수행될 때까지 대기한다 (backCache.entries 에 접근하면 expired event 가 발생한다)
        // NearCache 내에서 Expire 검사 Thread로 동작해야 합니다.
        await untilSuspending { !nearCoCache2.containsKey(key) }
        await untilSuspending { !nearCoCache1.containsKey(key) }

        output.capture() shouldContain "backCache의 cache entry가 expire 되었는지 검사합니다"

        backCoCache.containsKey(key).shouldBeFalse()
        nearCoCache1.containsKey(key).shouldBeFalse()
        nearCoCache2.containsKey(key).shouldBeFalse()
    }
}