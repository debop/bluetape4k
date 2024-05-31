package io.bluetape4k.coroutines.tests

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import kotlin.test.assertFailsWith

suspend inline fun <T> Flow<T>.assertEmpty() {
    toList().shouldBeEmpty()
}

suspend inline fun <T> Flow<T>.assertResult(expected: Flow<T>) {
    toList() shouldBeEqualTo expected.toList()
}

suspend inline fun <T> Flow<T>.assertResult(vararg values: T) {
    toList() shouldBeEqualTo values.toList()
}

suspend inline fun <T> Flow<T>.assertResultSet(vararg values: T) {
    toList().toSet() shouldBeEqualTo values.toSet()
}

suspend inline fun <T> Flow<T>.assertResultSet(values: Iterable<T>) {
    toList().toSet() shouldBeEqualTo values.toSet()
}


suspend inline fun <T, reified E: Throwable> Flow<T>.assertFailure(vararg values: T) {
    val list = mutableListOf<T>()
    assertFailsWith<E> {
        toList(list)
    }
    list shouldBeEqualTo values.toList()
}

suspend inline fun <reified E: Throwable> Flow<*>.assertError() {
    this.catch { it shouldBeInstanceOf E::class }.collect()
}
