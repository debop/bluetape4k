@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.extensions.utils.NULL_VALUE
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * Merges two [Flow]s into one [Flow] by combining each value from self with the latest value from the second [Flow], if any.
 * Values emitted by self before the second [Flow] has emitted any values will be omitted.
 *
 * @param other Second [Flow]
 * @param transform A transform function to apply to each value from self combined with the latest value from the second [Flow], if any.
 */
fun <A, B, R> Flow<A>.withLatestFrom(
    other: Flow<B>,
    transform: suspend (A, B) -> R,
): Flow<R> = flow {
    val otherRef = atomic<Any?>(null)

    try {
        coroutineScope {
            // other 을 collect 해서 가장 최신의 값을 otherRef 에 저장하도록 한다  
            launch(start = CoroutineStart.UNDISPATCHED) {
                other.collect { otherRef.value = it ?: NULL_VALUE }
            }

            // source 로부터 값이 emit 되면 otherRef의 값과 함께 transform을 호출하도록 한다.
            // 만약 otherRef 값이 null 이라면 collect 를 중단한다  
            collect { value: A ->
                emit(
                    transform(value, NULL_VALUE.unbox(otherRef.value ?: return@collect))
                )
            }
        }
    } finally {
        otherRef.value = null
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B> Flow<A>.withLatestFrom(other: Flow<B>): Flow<Pair<A, B>> =
    withLatestFrom(other) { a, b -> a to b }
