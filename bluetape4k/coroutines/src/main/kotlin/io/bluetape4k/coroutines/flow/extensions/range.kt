@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@JvmName("rangeOfInt")
fun range(start: Int, count: Int): Flow<Int> = (start until start + count).asFlow()

@JvmName("rangeOfLong")
fun range(start: Long, count: Int): Flow<Long> = (start until start + count).asFlow()

fun rangeInt(start: Int, count: Int): Flow<Int> = (start until start + count).asFlow()
fun rangeLong(start: Long, count: Int): Flow<Long> = (start until start + count).asFlow()

fun CharProgression.asFlow(): Flow<Char> = flow { this@asFlow.forEach { emit(it) } }
fun IntProgression.asFlow(): Flow<Int> = flow { this@asFlow.forEach { emit(it) } }
fun LongProgression.asFlow(): Flow<Long> = flow { this@asFlow.forEach { emit(it) } }
