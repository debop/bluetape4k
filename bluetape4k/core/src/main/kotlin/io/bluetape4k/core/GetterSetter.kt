package io.bluetape4k.core

/**
 * (T) -> V 를 나타내는 getter 기능을 표현한 interface 입니다.
 * @property getter Function1<K, V>
 */
interface GetterOperator<in K, out V> {
    val getter: (K) -> V
    operator fun get(key: K): V = getter(key)
}

fun <K, V> getterOperator(func: (K) -> V): GetterOperator<K, V> {
    return object: GetterOperator<K, V> {
        override val getter: (K) -> V
            get() = func
    }
}

interface SetterOperator<in K, in V> {
    val setter: (K, V) -> Unit
    operator fun set(key: K, value: V) {
        setter(key, value)
    }
}

fun <K, V> setterOperator(func: (K, V) -> Unit): SetterOperator<K, V> {
    return object: SetterOperator<K, V> {
        override val setter: (K, V) -> Unit
            get() = func
    }
}
