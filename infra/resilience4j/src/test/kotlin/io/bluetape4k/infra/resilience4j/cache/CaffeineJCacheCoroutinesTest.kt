package io.bluetape4k.infra.resilience4j.cache

import javax.cache.Cache

class CaffeineJCacheCoroutinesTest: AbstractJCacheCoroutinesTest() {

    override val jcache: Cache<String, String> =
        CaffeineJCacheProvider.getJCache("caffeine.coroutines")
}
