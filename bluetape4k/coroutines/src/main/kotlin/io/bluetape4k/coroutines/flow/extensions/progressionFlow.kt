@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.support.requireLe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


fun charFlowOf(start: Char, endInclusive: Char, step: Int = 1): Flow<Char> = flow {
    start.requireLe(endInclusive, "start")
    var current = start
    while (current <= endInclusive) {
        emit(current)
        current += step
    }
}

fun byteFlowOf(start: Byte, endInclusive: Byte, step: Byte = 1): Flow<Byte> = flow {
    start.requireLe(endInclusive, "start")

    var current = start
    while (current <= endInclusive) {
        emit(current)
        current = (current + step).toByte()
    }
}

fun intFlowOf(start: Int, endInclusive: Int, step: Int = 1): Flow<Int> = flow {
    start.requireLe(endInclusive, "start")
    var current = start
    while (current <= endInclusive) {
        emit(current)
        current += step
    }
}

fun longFlowOf(start: Long, endInclusive: Long, step: Long = 1L): Flow<Long> = flow {
    start.requireLe(endInclusive, "start")
    var current = start
    while (current <= endInclusive) {
        emit(current)
        current += step
    }
}

fun floatFlowOf(start: Float, endInclusive: Float, step: Float = 1.0F): Flow<Float> = flow {
    start.requireLe(endInclusive, "start")

    var current = start
    while (current <= endInclusive) {
        emit(current)
        current += step
    }
}

fun doubleFlowOf(start: Double, endInclusive: Double, step: Double = 1.0): Flow<Double> = flow {
    start.requireLe(endInclusive, "start")
    var current = start
    while (current <= endInclusive) {
        emit(current)
        current += step
    }
}
