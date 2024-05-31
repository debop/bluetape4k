package io.bluetape4k.core

import kotlinx.atomicfu.atomic

/**
 * [factory]를 이용하여 지연된 초기화를 수행하여 값을 제공하는 클래스입니다.
 *
 * ```
 * val lv = LazyValue { System.currentTimeMillis() }
 * // some code
 *
 * val timestamp = lv.value   // 이 때의 timestamp 값을 반환한다
 * ```
 *
 * @param T The type of value
 * @property factory The factory of value
 */
class LazyValue<T: Any>(private val factory: () -> T): AbstractValueObject() {

    private val initialized = atomic(false)
    val isInitialized: Boolean get() = initialized.value

    private var _hashCode: Int = 0

    val value: T by lazy {
        factory.invoke().apply {
            initialized.value = true
            _hashCode = this.hashCode()
        }
    }

    fun <S: Any> map(mapper: (T) -> S): LazyValue<S> = LazyValue { mapper(value) }

    fun <S: Any> flatMap(mapper: (T) -> LazyValue<S>): LazyValue<S> =
        LazyValue { mapper.invoke(value).value }

    override fun hashCode(): Int {
        return if (initialized.value) _hashCode else value.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other != null && super.equals(other)
    }

    override fun equalProperties(other: Any): Boolean {
        return other is LazyValue<*> && value == other.value
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("value", value)
    }
}
