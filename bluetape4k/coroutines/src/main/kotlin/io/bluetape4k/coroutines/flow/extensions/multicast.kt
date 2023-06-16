@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.extensions.subject.SubjectApi
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

/**
 * Shares a single collector towards the upstream source and multicasts
 * values to any number of consumers which then can produce the output
 * flow of values.
 */
fun <T, R> Flow<T>.multicast(
    subjectSupplier: () -> SubjectApi<T>,
    transform: suspend (Flow<T>) -> Flow<R>,
): Flow<R> =
    multicastInternal(this, subjectSupplier, transform)
// FlowMulticastFunction(this, subjectSupplier, transform)


internal fun <T, R> multicastInternal(
    source: Flow<T>,
    subjectSupplier: () -> SubjectApi<T>,
    transform: suspend (Flow<T>) -> Flow<R>,
): Flow<R> = flow {
    coroutineScope {
        val cancelled = atomic(false)
        val subject = subjectSupplier()
        val result = transform(subject)

        val inner = ResumableCollector<R>()

        // publish
        launch(start = CoroutineStart.UNDISPATCHED) {
            try {
                result.onCompletion { cancelled.value = true }
                    .collect {
                        inner.next(it)
                    }
                inner.complete()
            } catch (e: Throwable) {
                inner.error(e)
            }
        }

        // subject
        launch(start = CoroutineStart.UNDISPATCHED) {
            try {
                source.collect {
                    if (cancelled.value) {
                        throw CancellationException()
                    }
                    subject.emit(it)
                    if (cancelled.value) {
                        throw CancellationException()
                    }
                }
                subject.complete()
            } catch (e: Throwable) {
                subject.emitError(e)
            }
        }

        inner.drain(this@flow)
    }
}
