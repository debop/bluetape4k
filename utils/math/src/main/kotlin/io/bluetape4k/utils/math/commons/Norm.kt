package io.bluetape4k.utils.math.commons

import io.bluetape4k.utils.math.sumOfSq
import io.bluetape4k.utils.math.sumOfSquares

/**
 * 변량들의 제곱의 합을 구합니다.
 */
fun DoubleArray.norm(): Double = sumOfSq()

/**
 * 변량들의 제곱의 합을 구합니다.
 */
fun <N: Number> Sequence<N>.norm(): Double = sumOfSquares()

/**
 * 변량들의 제곱의 합을 구합니다.
 */
fun <N: Number> Iterable<N>.norm(): Double = sumOfSquares()
