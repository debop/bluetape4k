@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.extensions.utils.DONE_VALUE
import io.bluetape4k.coroutines.flow.extensions.utils.NULL_VALUE
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.ChannelResult
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Define leading and trailing behavior.
 */
enum class ThrottleBehavior {
    /**
     * Emits only the first item in each window.
     *
     * @see [kotlinx.coroutines.flow.debounce]
     * @see [kotlinx.coroutines.flow.sample]
     */
    LEADING,

    /**
     * Emits only the last item in each window.
     *
     * @see [kotlinx.coroutines.flow.debounce]
     * @see [kotlinx.coroutines.flow.sample]
     */
    TRAILING,

    /**
     * Emits both the first item and the last item in each window.
     */
    BOTH
}

val ThrottleBehavior.isLeading: Boolean
    get() = this == ThrottleBehavior.LEADING || this == ThrottleBehavior.BOTH

val ThrottleBehavior.isTrailing: Boolean
    get() = this == ThrottleBehavior.TRAILING || this == ThrottleBehavior.BOTH


fun <T> Flow<T>.throttleLeading(duration: Duration): Flow<T> =
    throttleTime(ThrottleBehavior.LEADING) { duration }

fun <T> Flow<T>.throttleLeading(timeMillis: Long): Flow<T> =
    throttleTime(ThrottleBehavior.LEADING) { timeMillis.milliseconds }

fun <T> Flow<T>.throttleLeading(durationSelector: (value: T) -> Duration): Flow<T> =
    throttleTime(ThrottleBehavior.LEADING, durationSelector)

fun <T> Flow<T>.throttleTrailing(timeMillis: Long): Flow<T> =
    throttleTime(ThrottleBehavior.TRAILING) { timeMillis.milliseconds }

fun <T> Flow<T>.throttleTrailing(duration: Duration): Flow<T> =
    throttleTime(ThrottleBehavior.TRAILING) { duration }

fun <T> Flow<T>.throttleTrailing(durationSelector: (value: T) -> Duration): Flow<T> =
    throttleTime(ThrottleBehavior.TRAILING, durationSelector)

fun <T> Flow<T>.throttleBoth(timeMillis: Long): Flow<T> =
    throttleTime(ThrottleBehavior.BOTH) { timeMillis.milliseconds }

fun <T> Flow<T>.throttleBoth(duration: Duration): Flow<T> =
    throttleTime(ThrottleBehavior.BOTH) { duration }

fun <T> Flow<T>.throttleBoth(durationSelector: (value: T) -> Duration): Flow<T> =
    throttleTime(ThrottleBehavior.BOTH, durationSelector)


/**
 * Source [Flow]에서 값을 방출하는 [Flow]를 반환한 다음,
 * [duration]기간 동안 후속 소스 값을 무시한 후, 방출한다.
 *
 * [kotlinx.coroutines.flow.debounce]와 유사하지만, [ThrottleBehavior]에 따라서 전, 후, 모두 값을 방출할 수 있다.
 *
 * * Example [ThrottleBehavior.LEADING]:
 *
 * ```kotlin
 * (1..10)
 *     .asFlow()
 *     .onEach { delay(200) }
 *     .throttleTime(500.milliseconds)
 * ```
 *
 * produces the following emissions
 *
 * ```text
 * 1, 4, 7, 10
 * ```
 *
 * * Example [ThrottleBehavior.TRAILING]:
 *
 * ```kotlin
 * (1..10)
 *     .asFlow()
 *     .onEach { delay(200) }
 *     .throttleTime(500.milliseconds, ThrottleBehavior.TRAILING)
 * ```
 *
 * produces the following emissions
 *
 * ```text
 * 3, 6, 9, 10
 * ```
 *
 * * Example [ThrottleBehavior.BOTH]:
 *
 * ```kotlin
 * (1..10)
 *     .asFlow()
 *     .onEach { delay(200) }
 *     .throttleTime(500.milliseconds, ThrottleBehavior.BOTH)
 * ```
 *
 * produces the following emissions
 *
 * ```text
 * 1, 3, 4, 6, 7, 9, 10
 * ```
 */
fun <T> Flow<T>.throttleTime(
    duration: Duration,
    throttleBehavior: ThrottleBehavior = ThrottleBehavior.LEADING,
): Flow<T> =
    throttleTime(throttleBehavior) { duration }


/**
 * Source [Flow]에서 값을 방출하는 [Flow]를 반환한 다음,
 * [timeMillis] 기간 동안 후속 소스 값을 무시한 후, 방출한다.
 *
 * [kotlinx.coroutines.flow.debounce]와 유사하지만, [ThrottleBehavior]에 따라서 전, 후, 모두 값을 방출할 수 있다.
 *
 * * Example [ThrottleBehavior.LEADING]:
 *
 * ```kotlin
 * (1..10)
 *     .asFlow()
 *     .onEach { delay(200) }
 *     .throttleTime(500.milliseconds)
 * ```
 *
 * produces the following emissions
 *
 * ```text
 * 1, 4, 7, 10
 * ```
 *
 * * Example [ThrottleBehavior.TRAILING]:
 *
 * ```kotlin
 * (1..10)
 *     .asFlow()
 *     .onEach { delay(200) }
 *     .throttleTime(500.milliseconds, ThrottleBehavior.TRAILING)
 * ```
 *
 * produces the following emissions
 *
 * ```text
 * 3, 6, 9, 10
 * ```
 *
 * * Example [ThrottleBehavior.BOTH]:
 *
 * ```kotlin
 * (1..10)
 *     .asFlow()
 *     .onEach { delay(200) }
 *     .throttleTime(500.milliseconds, ThrottleBehavior.BOTH)
 * ```
 *
 * produces the following emissions
 *
 * ```text
 * 1, 3, 4, 6, 7, 9, 10
 * ```
 */
fun <T> Flow<T>.throttleTime(
    timeMillis: Long,
    throttleBehavior: ThrottleBehavior = ThrottleBehavior.LEADING,
): Flow<T> =
    throttleTime(throttleBehavior) { timeMillis.milliseconds }


/**
 * Source [Flow]에서 값을 방출하는 [Flow]를 반환한 다음,
 * [durationSelector]가 지정한 기간 동안 후속 소스 값을 무시한 후, 방출한다.
 *
 * [kotlinx.coroutines.flow.debounce]와 유사하지만, [ThrottleBehavior]에 따라서 전, 후, 모두 값을 방출할 수 있다.
 *
 * * Example [ThrottleBehavior.LEADING]:
 *
 * ```kotlin
 * (1..10)
 *     .asFlow()
 *     .onEach { delay(200) }
 *     .throttleTime { 500.milliseconds }
 * ```
 *
 * produces the following emissions
 *
 * ```text
 * 1, 4, 7, 10
 * ```
 *
 * * Example [ThrottleBehavior.TRAILING]:
 *
 * ```kotlin
 * (1..10)
 *     .asFlow()
 *     .onEach { delay(200) }
 *     .throttleTime(ThrottleBehavior.TRAILING) { 500.milliseconds }
 * ```
 *
 * produces the following emissions
 *
 * ```text
 * 3, 6, 9, 10
 * ```
 *
 * * Example [ThrottleBehavior.BOTH]:
 *
 * ```kotlin
 * (1..10)
 *     .asFlow()
 *     .onEach { delay(200) }
 *     .throttleTime(ThrottleBehavior.BOTH) { 500.milliseconds }
 * ```
 *
 * produces the following emissions
 *
 * ```text
 * 1, 3, 4, 6, 7, 9, 10
 * ```
 */
fun <T> Flow<T>.throttleTime(
    throttleBehavior: ThrottleBehavior = ThrottleBehavior.LEADING,
    durationSelector: (value: T) -> Duration,
): Flow<T> = flow {
    val leading = throttleBehavior.isLeading
    val trailing = throttleBehavior.isTrailing
    val downstream = this

    coroutineScope {
        val scope = this

        // Produce the values using the default (rendezvous) channel
        val values: ReceiveChannel<Any> = produce {
            collect { value ->
                send(value ?: NULL_VALUE)
            }
        }

        var lastValue: Any? = null
        var throttled: Job? = null

        suspend fun trySend() {
            lastValue?.let { consumed ->
                check(lastValue !== DONE_VALUE)

                // Ensure we clear out our lastValue
                // before we emit, otherwise reentrant code can cause
                // issues here.
                lastValue = null // Consume the value
                return@let downstream.emit(NULL_VALUE.unbox(consumed))
            }
        }

        val onWindowClosed = suspend {
            throttled = null
            if (trailing) {
                trySend()
            }
        }

        // Now consume the values until the original flow is complete.
        while (lastValue !== DONE_VALUE) {
            kotlinx.coroutines.selects.select<Unit> {
                // When a throttling window ends, send the value if there is a pending value.
                throttled?.onJoin?.invoke(onWindowClosed)

                values.onReceiveCatching { result: ChannelResult<Any> ->
                    result
                        .onSuccess { value: Any ->
                            lastValue = value

                            // If we are not within a throttling window, immediately send the value (if leading is true)
                            // and then start throttling.

                            throttled?.let { return@onSuccess }
                            if (leading) {
                                trySend()
                            }
                            when (val duration = durationSelector(NULL_VALUE.unbox(value))) {
                                Duration.ZERO -> onWindowClosed()
                                else          -> throttled = scope.launch { delay(duration) }
                            }
                        }
                        .onFailure { error ->
                            error?.let { throw error }

                            // Once the original flow has completed, there may still be a pending value
                            // waiting to be emitted. If so, wait for the throttling window to end and then
                            // send it. That will complete this throttled flow.
                            if (trailing && lastValue != null) {
                                throttled?.run {
                                    throttled = null
                                    this.join()
                                    trySend()
                                }
                            }

                            lastValue = DONE_VALUE
                        }
                }
            }
        }

        throttled?.run {
            throttled = null
            cancelAndJoin()
        }
    }
}
