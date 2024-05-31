@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@JvmName("flowRangeOfInt")
fun flowRangeOf(start: Int, count: Int): Flow<Int> = (start until start + count).asFlow()

@JvmName("flowRangeOfLong")
fun flowRangeOf(start: Long, count: Int): Flow<Long> = (start until start + count).asFlow()

fun flowRangeInt(start: Int, count: Int): Flow<Int> = (start until start + count).asFlow()
fun flowRangeLong(start: Long, count: Int): Flow<Long> = (start until start + count).asFlow()

fun CharProgression.asFlow(): Flow<Char> = flow { this@asFlow.asSequence().forEach { emit(it) } }
fun IntProgression.asFlow(): Flow<Int> = flow { this@asFlow.asSequence().forEach { emit(it) } }
fun LongProgression.asFlow(): Flow<Long> = flow { this@asFlow.asSequence().forEach { emit(it) } }
