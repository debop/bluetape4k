package io.bluetape4k.coroutines

import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.core.ValueObject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

/**
 * [DeferredValue] 인스턴스를 생성합니다.
 *
 * @param factory 값을 생성하는 suspend 함수
 * @return [DeferredValue] 인스턴스
 */
fun <T> deferredValueOf(factory: suspend () -> T): DeferredValue<T> = DeferredValue(factory)

/**
 * 값 계산을 지연해서 수행하는 클래스입니다.
 *
 * @property factory 값 계산을 수행하는 함수
 */
data class DeferredValue<T>(internal inline val factory: suspend () -> T): DefaultCoroutineScope(), ValueObject {

    private val deferredValue: Deferred<T> = async { factory() }

    val value: T by lazy { runBlocking { deferredValue.await() } }

    suspend fun await(): T = deferredValue.await()

    val isCompleted: Boolean get() = deferredValue.isCompleted
    val isActive: Boolean get() = deferredValue.isActive
    val isCancelled: Boolean get() = deferredValue.isCancelled

    override fun equals(other: Any?): Boolean {
        return other is DeferredValue<*> && deferredValue == other.deferredValue
    }

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String {
        return ToStringBuilder(this)
            .add("value", value)
            .toString()
    }

    inline fun <S> map(crossinline mapper: suspend (T) -> S): DeferredValue<S> =
        DeferredValue { mapper(await()) }

    inline fun <S> flatMap(crossinline flatter: (T) -> DeferredValue<S>): DeferredValue<S> =
        DeferredValue { flatter(await()).await() }
}
