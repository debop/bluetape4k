package io.bluetape4k.core

class LazyValue<T : Any>(private val factory: () -> T) : AbstractValueObject() {

    @Volatile
    private var initialized: Boolean = false

    val isInitialized: Boolean get() = initialized

    val value: T by lazy { factory.invoke().apply { initialized = true } }

    fun <S : Any> map(mapper: (T) -> S): LazyValue<S> = LazyValue { mapper(value) }

    fun <S : Any> flatMap(mapper: (T) -> LazyValue<S>): LazyValue<S> =
        LazyValue { mapper.invoke(value).value }

    override fun hashCode(): Int = value.hashCode()

    override fun equalProperties(other: Any): Boolean {
        return other is LazyValue<*> && value == other.value
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("value", value)
    }
}
