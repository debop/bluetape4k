@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.extensions.subject.ReplaySubject
import kotlinx.coroutines.flow.Flow
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * Shares a single collector towards the upstream source and multicasts
 * up to a given [maxSize] number of cached values to any number of
 * consumers which then can produce the output
 * flow of values.
 */
fun <T, R> Flow<T>.replay(transform: suspend (Flow<T>) -> Flow<R>): Flow<R> =
    replay({ ReplaySubject() }, transform)

fun <T, R> Flow<T>.replay(maxSize: Int, transform: suspend (Flow<T>) -> Flow<R>): Flow<R> =
    replay({ ReplaySubject(maxSize) }, transform)

fun <T, R> Flow<T>.replay(maxTimeout: Duration, transform: suspend (Flow<T>) -> Flow<R>): Flow<R> =
    replay({ ReplaySubject(maxTimeout.toMillis(), TimeUnit.MILLISECONDS) }, transform)

fun <T, R> Flow<T>.replay(maxSize: Int, maxTimeout: Duration, transform: suspend (Flow<T>) -> Flow<R>): Flow<R> =
    replay({ ReplaySubject(maxSize, maxTimeout.toMillis(), TimeUnit.MILLISECONDS) }, transform)

fun <T, R> Flow<T>.replay(
    maxSize: Int,
    maxTimeout: Duration,
    timeSource: (TimeUnit) -> Long,
    transform: suspend (Flow<T>) -> Flow<R>,
): Flow<R> =
    replay({ ReplaySubject(maxSize, maxTimeout.toMillis(), TimeUnit.MILLISECONDS, timeSource) }, transform)

fun <T, R> Flow<T>.replay(
    replaySubjectSupplier: () -> ReplaySubject<T>,
    transform: suspend (Flow<T>) -> Flow<R>,
): Flow<R> =
    multicastInternal(this, replaySubjectSupplier, transform)
// FlowMulticastFunction(this, replaySubjectSupplier, transform)
