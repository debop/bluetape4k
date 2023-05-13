package io.bluetape4k.coroutines.flow.extensions.subject

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

/**
 * Base interface for suspendable push signals emit, emitError and complete.
 */
interface SubjectApi<T>: FlowCollector<T>, Flow<T> {

    /**
     * Returns true if this subject has collectors waiting for data.
     */
    val hasCollectors: Boolean

    /**
     * Returns the number of collectors waiting for data.
     */
    val collectorCount: Int

    /**
     * Signal an Throwable to the collector.
     */
    suspend fun emitError(ex: Throwable?)

    /**
     * Indicate no further items will be produced.
     */
    suspend fun complete()
}
