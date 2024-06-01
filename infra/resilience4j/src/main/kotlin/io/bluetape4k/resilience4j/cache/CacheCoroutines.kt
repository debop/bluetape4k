package io.bluetape4k.resilience4j.cache

import io.github.resilience4j.cache.Cache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend inline fun <K, V> withCache(
    cache: Cache<K, V>,
    key: K,
    crossinline block: suspend (K) -> V,
): V {
    return cache.executeSuspendedFunction(key, block)
}

inline fun <K, V> Cache<K, V>.decorateSuspendedFunction(
    crossinline block: suspend (K) -> V,
): suspend (K) -> V = { key: K ->
    executeSuspendedFunction(key, block)
}

suspend inline fun <K, V> Cache<K, V>.executeSuspendedFunction(
    key: K,
    crossinline block: suspend (K) -> V,
): V = suspendCoroutine { cont ->

    val cachedValue = runCatching { computeIfAbsent(key!!) { null } }.getOrNull()
    if (cachedValue != null) {
        cont.resume(cachedValue)
    } else {
        // Load cache value
        val result = runCatching { runBlocking { withContext(Dispatchers.IO) { block(key) } } }
        if (result.isSuccess) {
            if (result.getOrNull() != null) {
                this.computeIfAbsent(key!!) { result.getOrNull() }
            }
            cont.resumeWith(result)
        } else {
            cont.resumeWithException(result.exceptionOrNull()!!)
        }
    }
}
