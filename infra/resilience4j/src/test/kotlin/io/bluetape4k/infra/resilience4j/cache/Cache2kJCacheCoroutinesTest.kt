package io.bluetape4k.infra.resilience4j.cache

import javax.cache.Cache

class Cache2kJCacheCoroutinesTest: AbstractJCacheCoroutinesTest() {

    override val jcache: Cache<String, String> = Cache2kJCacheProvider.getJCache("cache2k.coroutines")

}
