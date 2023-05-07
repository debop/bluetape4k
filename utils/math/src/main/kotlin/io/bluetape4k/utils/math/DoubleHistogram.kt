package io.bluetape4k.utils.math

inline fun <T: Any> Sequence<T>.binByDouble(
    binSize: Double,
    valueMapper: (T) -> Double,
    rangeStart: Double? = null,
): BinModel<List<T>, Double> = asIterable().binByDouble(binSize, valueMapper, rangeStart)

inline fun <T: Any> Iterable<T>.binByDouble(
    binSize: Double,
    valueMapper: (T) -> Double,
    rangeStart: Double? = null,
): BinModel<List<T>, Double> = binByDouble(binSize, valueMapper, { it }, rangeStart)

inline fun <T: Any, G: Any> Iterable<T>.binByDouble(
    binSize: Double,
    valueMapper: (T) -> Double,
    crossinline groupOp: (List<T>) -> G,
    rangeStart: Double? = null,
): BinModel<G, Double> {
    // TODO: 수형마다 따로 구현할 필요없이, binByComparable을 호출해도 되지 않을까? 검증 필요
    return binByComparable({ it + binSize }, valueMapper, groupOp, rangeStart)
}
