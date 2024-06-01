package io.bluetape4k.http.hc5.cache

import org.apache.hc.client5.http.cache.HttpCacheStorage
import org.apache.hc.client5.http.impl.cache.CachingHttpClientBuilder
import org.apache.hc.client5.http.impl.cache.CachingHttpClients
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import java.io.File

inline fun cachingHttpClient(
    initializer: CachingHttpClientBuilder.() -> Unit,
): CloseableHttpClient {
    return CachingHttpClientBuilder.create().apply(initializer).build()
}

inline fun cachingHttpClient(
    cacheStorage: HttpCacheStorage,
    initializer: CachingHttpClientBuilder.() -> Unit,
): CloseableHttpClient {
    return CachingHttpClientBuilder.create()
        .setHttpCacheStorage(cacheStorage)
        .apply(initializer)
        .build()
}

fun memoryCachingHttpClientOf(): CloseableHttpClient =
    CachingHttpClients.createMemoryBound()

fun fileCachingHttpClientOf(cacheDir: File): CloseableHttpClient =
    CachingHttpClients.createFileBound(cacheDir)
