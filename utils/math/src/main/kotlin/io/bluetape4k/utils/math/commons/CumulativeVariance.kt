package io.bluetape4k.utils.math.commons

/**
 * 시퀀스의 누적 분산을 계산합니다.
 */
fun <N: Number> Sequence<N>.cumulativeVariance(): Sequence<Double> = sequence {
    var n = 1
    var sum = first().toDouble()
    var sumSqrt = sum.square()

    drop(1).forEach {
        val curr = it.toDouble()
        n++
        sum += curr
        sumSqrt += curr.square()

        yield((sumSqrt - sum.square() / n) / (n - 1))
    }
}

/**
 * Collection의 누적 분산을 계산합니다.
 */
fun <N: Number> Iterable<N>.cumulativeVariance(): List<Double> =
    asSequence().cumulativeVariance().toList()
