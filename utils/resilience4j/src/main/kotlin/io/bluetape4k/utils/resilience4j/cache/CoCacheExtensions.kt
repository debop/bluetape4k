package io.bluetape4k.utils.resilience4j.cache

suspend fun <K: Any, V> withCaache(
    cache: CoCache<K, V>,
    cacheKey: K,
    loader: suspend () -> V,
): V {
    return cache.computeIfAbsent(cacheKey, loader)
}

fun <K: Any, V> CoCache<K, V>.decorateSuspendedSupplier(
    loader: suspend () -> V,
): suspend (K) -> V = { cacheKey: K ->
    executeSuspendedFunction(cacheKey, loader)
}

fun <K: Any, V> CoCache<K, V>.decorateSuspendedFunction(
    loader: suspend (K) -> V,
): suspend (K) -> V = { cacheKey: K ->
    executeSuspendedFunction(cacheKey) { loader.invoke(cacheKey) }
}

suspend fun <K: Any, V> CoCache<K, V>.executeSuspendedFunction(cacheKey: K, loader: suspend () -> V): V =
    computeIfAbsent(cacheKey, loader)
