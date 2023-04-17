package io.bluetape4k.utils.resilience4j

import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException

/**
 * [CoroutineContext]이 취소되었는지 여부
 *
 * @param coroutineContext
 * @param error
 * @return
 */
internal fun isCancellation(coroutineContext: CoroutineContext, error: Throwable? = null): Boolean {

    // If job is missing then there is no cancellation
    val job = coroutineContext[Job] ?: return false

    return job.isCancelled || (error != null && error is CancellationException)
}
