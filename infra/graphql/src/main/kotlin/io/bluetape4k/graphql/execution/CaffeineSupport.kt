package io.bluetape4k.graphql.execution

import com.github.benmanes.caffeine.cache.AsyncCache
import io.bluetape4k.cache.caffeine.caffeine
import java.util.concurrent.TimeUnit

internal fun <K, V> caffeineAsyncCacheOf(
    maximumSize: Long = 25000,
    expireAfterWrite: Long = 1,
    timeUnit: TimeUnit = TimeUnit.HOURS,
): AsyncCache<K, V> = caffeine {
    maximumSize(maximumSize)
    expireAfterWrite(expireAfterWrite, timeUnit)
}.buildAsync()
