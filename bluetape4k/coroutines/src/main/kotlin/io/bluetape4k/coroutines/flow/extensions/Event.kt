package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.exception.FlowNoElementException

/**
 * reactive signal types: value, error and complete 을 가진 signal 정보를 제공합니다.
 */
sealed interface Event<out T> {

    data class Value<out T>(val value: T): Event<T> {
        override fun toString(): String = "Event.Value($value)"
    }

    data class Error(val error: Throwable): Event<Nothing> {
        override fun toString(): String = "Event.Error($error)"
    }

    object Complete: Event<Nothing> {
        override fun toString(): String = "Event.Complete"
    }
}

inline fun <T, R> Event<T>.map(transform: (T) -> R): Event<R> = when (this) {
    is Event.Value -> Event.Value(transform(value))
    is Event.Error -> this
    Event.Complete -> Event.Complete
}

inline fun <T, R> Event<T>.flatMap(transform: (T) -> Event<R>): Event<R> = when (this) {
    is Event.Value -> transform(value)
    is Event.Error -> this
    Event.Complete -> Event.Complete
}

fun <T> Event<T>.valueOrNull(): T? = valueOrDefault(null)

fun <T> Event<T>.valueOrDefault(defaultValue: T): T = valueOrElse { defaultValue }

fun <T> Event<T>.valueOrThrow(): T =
    valueOrElse { throw (it ?: FlowNoElementException("$this has no value!")) }


inline fun <T> Event<T>.valueOrElse(defaultValue: (Throwable?) -> T): T = when (this) {
    is Event.Value -> value
    is Event.Error -> defaultValue(error)
    Event.Complete -> defaultValue(null)
}

fun <T> Event<T>.errorOrNull(): Throwable? = when (this) {
    is Event.Value -> null
    is Event.Error -> error
    Event.Complete -> null
}

fun <T> Event<T>.errorOrThrow(): Throwable =
    errorOrNull() ?: throw FlowNoElementException("$this has no error!")
