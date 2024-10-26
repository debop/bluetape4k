package io.bluetape4k.graphql.execution

import com.github.benmanes.caffeine.cache.AsyncCache
import com.netflix.graphql.dgs.apq.AutomatedPersistedQueryCacheAdapter
import graphql.ExecutionInput
import graphql.execution.preparsed.PreparsedDocumentEntry
import graphql.execution.preparsed.persisted.PersistedQueryCacheMiss
import io.bluetape4k.logging.KLogging
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

/**
 * Query에 해당하는 [PreparsedDocumentEntry]를 캐시하는 [AutomatedPersistedQueryCacheAdapter] 구현체.
 *
 * @property cache Caffeign [AsyncCache] instance
 */
class AutomatedPersistedQueryCaffeineAsyncCache(
    private val cache: AsyncCache<String, PreparsedDocumentEntry> = caffeineAsyncCacheOf(),
): AutomatedPersistedQueryCacheAdapter() {

    companion object: KLogging()

    override fun getFromCache(
        key: String,
        documentEntrySupplier: Supplier<PreparsedDocumentEntry>,
    ): PreparsedDocumentEntry? {
        return cache.get(key) { _ -> documentEntrySupplier.get() }
            .orTimeout(5, TimeUnit.SECONDS)
            .get()
    }

    override fun getPersistedQueryDocumentAsync(
        persistedQueryId: Any,
        executionInput: ExecutionInput,
        onCacheMiss: PersistedQueryCacheMiss,
    ): CompletableFuture<PreparsedDocumentEntry> {
        return CompletableFuture.supplyAsync {
            getPersistedQueryDocument(persistedQueryId, executionInput, onCacheMiss)
        }
    }
}
