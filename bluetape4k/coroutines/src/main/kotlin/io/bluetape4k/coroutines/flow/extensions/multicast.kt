@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.extensions.internal.FlowMulticastFunction
import io.bluetape4k.coroutines.flow.extensions.subject.SubjectApi
import kotlinx.coroutines.flow.Flow

/**
 * Shares a single collector towards the upstream source and multicasts
 * values to any number of consumers which then can produce the output
 * flow of values.
 */
fun <T, R> Flow<T>.multicast(
    subjectSupplier: () -> SubjectApi<T>,
    transform: suspend (Flow<T>) -> Flow<R>,
): Flow<R> =
    FlowMulticastFunction(this, subjectSupplier, transform)
