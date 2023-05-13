package io.bluetape4k.core

import kotlinx.atomicfu.atomic

class LazyValue<T: Any>(private val factory: () -> T): AbstractValueObject() {

    var isInitialized: Boolean by atomic(false)
        private set

    val value: T by lazy { factory.invoke().apply { isInitialized = true } }

    fun <S: Any> map(mapper: (T) -> S): LazyValue<S> = LazyValue { mapper(value) }

    fun <S: Any> flatMap(mapper: (T) -> LazyValue<S>): LazyValue<S> =
        LazyValue { mapper.invoke(value).value }

    override fun hashCode(): Int = value.hashCode()

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
