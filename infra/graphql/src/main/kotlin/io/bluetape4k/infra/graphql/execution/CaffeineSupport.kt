package io.bluetape4k.infra.graphql.execution

import com.github.benmanes.caffeine.cache.AsyncCache
import io.bluetape4k.infra.cache.caffeine.Caffeine
import java.util.concurrent.TimeUnit

internal fun <K, V> caffeineAsyncCacheOf(
    maximumSize: Long = 25000,
    expireAfterWrite: Long = 1,
    timeUnit: TimeUnit = TimeUnit.HOURS,
): AsyncCache<K, V> = Caffeine {
    maximumSize(maximumSize)
    expireAfterWrite(expireAfterWrite, timeUnit)
}.buildAsync()
