@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * [f1], [f2], [fs] 순서대로 collect 합니다.
 */
fun <T> concat(f1: Flow<T>, f2: Flow<T>, vararg fs: Flow<T>): Flow<T> = flow {
    emitAll(f1)
    emitAll(f2)
    fs.forEach { f ->
        emitAll(f)
    }
}

/**
 * source flow 를 모두 collect 하고난 후, [f1], [fs] 를 collect 합니다.
 */
fun <T> Flow<T>.concatWith(f1: Flow<T>, vararg fs: Flow<T>): Flow<T> =
    concat(this, f1, *fs)

fun <T> Iterable<Flow<T>>.concat(): Flow<T> = flow {
    forEach { f -> emitAll(f) }
}

fun <T> Flow<T>.startWith(item: T, vararg items: T): Flow<T> =
    concat(
        flow {
            emit(item)
            emitAll(items.asFlow())
        },
        this
    )

fun <T> Flow<T>.startWith(valueSupplier: suspend () -> T): Flow<T> =
    concat(
        flow { emit(valueSupplier()) },
        this
    )

fun <T> Flow<T>.startWith(f1: Flow<T>, vararg fs: Flow<T>): Flow<T> = flow {
    emitAll(f1)
    fs.forEach { emitAll(it) }
    emitAll(this@startWith)
}

fun <T> Flow<T>.endWith(item: T, vararg items: T): Flow<T> =
    concat(
        this,
        flow {
            emit(item)
            emitAll(items.asFlow())
        })

fun <T> Flow<T>.endWith(f1: Flow<T>, vararg fs: Flow<T>): Flow<T> = flow {
    emitAll(this@endWith)
    emitAll(f1)
    fs.forEach { emitAll(it) }
}
