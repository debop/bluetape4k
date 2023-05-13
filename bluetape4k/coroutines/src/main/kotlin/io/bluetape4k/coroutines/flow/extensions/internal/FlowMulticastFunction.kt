package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.coroutines.flow.extensions.ResumableCollector
import io.bluetape4k.coroutines.flow.extensions.subject.SubjectApi
import io.bluetape4k.logging.KLogging
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException


internal class FlowMulticastFunction<T, R>(
    private val source: Flow<T>,
    private val subjectSupplier: () -> SubjectApi<T>,
    private val transform: suspend (Flow<T>) -> Flow<R>,
): AbstractFlow<R>() {

    companion object: KLogging()

    override suspend fun collectSafely(collector: FlowCollector<R>) {
        coroutineScope {
            var cancelled by atomic(false)
            val subject = subjectSupplier()
            val result = transform(subject)

            val inner = ResumableCollector<R>()

            // publish
            launch {
                try {
                    result.onCompletion { cancelled = true }
                        .collect {
                            inner.next(it)
                        }
                    inner.complete()
                } catch (e: Throwable) {
                    inner.error(e)
                }
            }

            // subject
            launch {
                try {
                    source.collect {
                        if (cancelled) {
                            throw CancellationException()
                        }
                        subject.emit(it)
                        if (cancelled) {
                            throw CancellationException()
                        }
                    }
                    subject.complete()
                } catch (e: Throwable) {
                    subject.emitError(e)
                }
            }

            inner.drain(collector)
        }
    }
}
