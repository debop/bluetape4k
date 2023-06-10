package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.extensions.internal.FlowMulticastFunction
import io.bluetape4k.coroutines.flow.extensions.subject.MulticastSubject
import io.bluetape4k.coroutines.flow.extensions.subject.PublishSubject
import kotlinx.coroutines.flow.Flow


/**
 * Shares a single collector towards the upstream source and multicasts
 * values to any number of consumers which then can produce the output
 * flow of values.
 *
 * Note that due to how coroutines/[Flow] are implemented, it is not guaranteed
 * the [transform] function connects the upstream with the downstream in time,
 * causing item loss or even run-to-completion without any single upstream item
 * being collected and transformed. To avoid such scenarios, use the
 * `publish(expectedCollectors)` overload.
 */
fun <T, R> Flow<T>.publish(transform: suspend (Flow<T>) -> Flow<R>): Flow<R> =
    FlowMulticastFunction(this, { PublishSubject() }, transform)

/**
 * Shares a single collector towards the upstream source and multicasts
 * values to any number of consumers which then can produce the output
 * flow of values.
 *
 * Note that due to how coroutines/[Flow] are implemented, it is not guaranteed
 * the [transform] function connects the upstream with the downstream in time,
 * causing item loss or even run-to-completion without any single upstream item
 * being collected and transformed. To avoid such scenarios, specify the
 * [expectedCollectors] to delay the collection of the upstream until the number
 * of inner collectors has reached the specified number.
 *
 * @param expectedCollectors the number of collectors to wait for before resuming the source, allowing
 * the desired number of collectors to arrive and be ready for the upstream items
 */
fun <T, R> Flow<T>.publish(
    expectedCollectors: Int = 3,
    transform: suspend (Flow<T>) -> Flow<R>,
): Flow<R> {
    return FlowMulticastFunction(
        this,
        { MulticastSubject(expectedCollectors.coerceAtLeast(1)) },
        transform
    )
}
