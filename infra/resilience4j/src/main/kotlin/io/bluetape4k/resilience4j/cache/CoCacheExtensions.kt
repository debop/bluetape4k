package io.bluetape4k.resilience4j.cache

suspend fun <K, V> withCaache(
    cache: CoCache<K, V>,
    cacheKey: K,
    loader: suspend () -> V,
): V {
    return cache.computeIfAbsent(cacheKey, loader)
}

fun <K, V> CoCache<K, V>.decorateSuspendedSupplier(
    loader: suspend () -> V,
): suspend (K) -> V = { cacheKey: K ->
    executeSuspendedFunction(cacheKey, loader)
}

inline fun <K, V> CoCache<K, V>.decorateSuspendedFunction(
    crossinline loader: suspend (K) -> V,
): suspend (K) -> V = { cacheKey: K ->
    executeSuspendedFunction(cacheKey) { loader(cacheKey) }
}

suspend inline fun <K, V> CoCache<K, V>.executeSuspendedFunction(
    cacheKey: K,
    crossinline loader: suspend () -> V,
): V {
    return computeIfAbsent(cacheKey) { loader() }
}
