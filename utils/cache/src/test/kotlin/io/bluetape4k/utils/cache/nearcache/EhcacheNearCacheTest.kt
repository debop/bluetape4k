package io.bluetape4k.utils.cache.nearcache

import io.bluetape4k.codec.encodeBase62
import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.cache.jcache.JCache
import io.bluetape4k.utils.cache.jcache.JCaching
import io.bluetape4k.utils.cache.jcache.jcacheConfiguration
import java.util.UUID
import javax.cache.expiry.EternalExpiryPolicy

class EhcacheNearCacheTest: AbstractNearCacheTest() {

    companion object: KLogging() {
        private const val TEST_SIZE = 10
    }

    override val backCache: JCache<String, Any> = JCaching.EhCache.getOrCreate(
        name = "back-cache-" + UUID.randomUUID().encodeBase62(),
        configuration = jcacheConfiguration {
            // setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration(MILLISECONDS, 50)))
            setExpiryPolicyFactory(EternalExpiryPolicy.factoryOf())
        }
    )
}
