@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun range(start: Int, count: Int): Flow<Int> = (start until start + count).asFlow()
fun range(start: Long, count: Int): Flow<Long> = (start until start + count).asFlow()

@Deprecated("use range", replaceWith = ReplaceWith("range(start, count)"))
fun flowOfRange(start: Int, count: Int): Flow<Int> = flowOfIntRange(start, count)

@Deprecated("use range", replaceWith = ReplaceWith("range(start, count)"))
fun flowOfIntRange(start: Int, count: Int): Flow<Int> =
    (start until start + count).asFlow()

@Deprecated("use range", replaceWith = ReplaceWith("range(start, count)"))
fun flowOfLongRange(start: Long, count: Int): Flow<Long> =
    (start until start + count).asFlow()

fun CharProgression.asFlow(): Flow<Char> = flow { this@asFlow.forEach { emit(it) } }
fun IntProgression.asFlow(): Flow<Int> = flow { this@asFlow.forEach { emit(it) } }
fun LongProgression.asFlow(): Flow<Long> = flow { this@asFlow.forEach { emit(it) } }
