package io.bluetape4k.coroutines.tests

import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.coroutines.flow.eclipse.toFastList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import kotlin.test.assertFailsWith

suspend inline fun <T> Flow<T>.assertEmpty() {
    toFastList().shouldBeEmpty()
}

suspend inline fun <T> Flow<T>.assertResult(vararg values: T) {
    toFastList() shouldBeEqualTo values.toFastList()
}

suspend inline fun <T> Flow<T>.assertResultSet(vararg values: T) {
    toSet() shouldBeEqualTo values.toSet()
}

suspend inline fun <T, reified E: Throwable> Flow<T>.assertFailure(vararg values: T) {
    val list = arrayListOf<T>()
    assertFailsWith<E> {
        toList(list)
    }
    list shouldBeEqualTo values.asList()
}

suspend inline fun <reified E: Throwable> Flow<*>.assertError() {
    this.catch { it shouldBeInstanceOf E::class }.collect()
}
