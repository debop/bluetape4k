package io.bluetape4k.http.hc5.cache

import org.apache.hc.client5.http.cache.HttpAsyncCacheStorage
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient
import org.apache.hc.client5.http.impl.cache.CachingHttpAsyncClientBuilder
import org.apache.hc.client5.http.impl.cache.CachingHttpAsyncClients
import java.io.File


inline fun cachingHttpAsyncClient(
    initializer: CachingHttpAsyncClientBuilder.() -> Unit,
): CloseableHttpAsyncClient {
    return CachingHttpAsyncClientBuilder.create().apply(initializer).build()
}

inline fun cachingHttpAsyncClient(
    cacheStorage: HttpAsyncCacheStorage,
    initializer: CachingHttpAsyncClientBuilder.() -> Unit,
): CloseableHttpAsyncClient {
    return CachingHttpAsyncClientBuilder.create()
        .setHttpCacheStorage(cacheStorage)
        .apply(initializer)
        .build()
}

fun memoryCachingHttpAsyncClientOf(): CloseableHttpAsyncClient =
    CachingHttpAsyncClients.createMemoryBound()

fun fileCachingHttpAsyncClientOf(cacheDir: File): CloseableHttpAsyncClient =
    CachingHttpAsyncClients.createFileBound(cacheDir)
