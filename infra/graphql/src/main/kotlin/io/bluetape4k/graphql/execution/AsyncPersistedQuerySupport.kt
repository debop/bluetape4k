package io.bluetape4k.graphql.execution

import com.github.benmanes.caffeine.cache.AsyncCache
import graphql.ExecutionInput
import graphql.execution.preparsed.PreparsedDocumentEntry
import graphql.execution.preparsed.persisted.PersistedQuerySupport
import io.bluetape4k.logging.KLogging
import java.util.*

/**
 * Preparsed document 를 Caffeine [AsyncCache]에 캐싱하는 Provider
 *
 * @see [graphql.execution.preparsed.persisted.ApolloPersistedQuerySupport]
 * @see [graphql.execution.preparsed.persisted.AutomatedPersistedQueryCacheAdapter]
 *
 * @property cache
 */
class AsyncPersistedQuerySupport(
    private val cache: AsyncCache<String, PreparsedDocumentEntry> = caffeineAsyncCacheOf(),
): PersistedQuerySupport(AutomatedPersistedQueryCaffeineAsyncCache(cache)) {

    companion object: KLogging()

    override fun getPersistedQueryId(executionInput: ExecutionInput): Optional<Any> {
        return Optional.ofNullable(executionInput.query)
    }
}
