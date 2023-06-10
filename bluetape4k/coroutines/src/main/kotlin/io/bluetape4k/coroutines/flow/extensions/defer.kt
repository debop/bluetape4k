package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

fun <T> defer(itemFactory: suspend () -> Flow<T>): Flow<T> = flow { emitAll(itemFactory.invoke()) }
