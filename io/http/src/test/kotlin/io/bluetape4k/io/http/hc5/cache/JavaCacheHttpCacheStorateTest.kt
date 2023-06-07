package io.bluetape4k.io.http.hc5.cache

import io.bluetape4k.infra.cache.jcache.JCaching
import org.apache.hc.client5.http.cache.HttpCacheStorage
import org.apache.hc.client5.http.cache.HttpCacheStorageEntry
import javax.cache.Cache

class JavaCacheHttpCacheStorateTest: AbstractHttpCacheStorageTest() {

    private val jcache: Cache<String, HttpCacheStorageEntry> = JCaching.Caffeine.getOrCreate("http-cache")

    override fun createCacheStorage(): HttpCacheStorage {
        return JavaCacheHttpCacheStorage.createObjectCache(jcache)
    }

}
