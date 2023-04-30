package io.bluetape4k.infra.resilience4j.cache

import io.github.resilience4j.cache.Cache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <K: Any, V> withCache(cache: Cache<K, V>, key: K, block: suspend (K) -> V): V =
    cache.executeSuspendedFunction(key, block)

fun <K: Any, V> Cache<K, V>.decorateSuspendedFunction(
    block: suspend (K) -> V,
): suspend (K) -> V = { key: K ->
    executeSuspendedFunction(key, block)
}

suspend fun <K: Any, V> Cache<K, V>.executeSuspendedFunction(
    key: K,
    block: suspend (K) -> V,
): V = suspendCoroutine { cont ->

    val cachedValue = runCatching { computeIfAbsent(key) { null } }.getOrNull()
    if (cachedValue != null) {
        cont.resume(cachedValue)
    } else {
        // Load cache value
        val result = runCatching { runBlocking { withContext(Dispatchers.IO) { block(key) } } }
        if (result.isSuccess) {
            if (result.getOrNull() != null) {
                this.computeIfAbsent(key) { result.getOrNull() }
            }
            cont.resumeWith(result)
        } else {
            cont.resumeWithException(result.exceptionOrNull()!!)
        }
    }
}
