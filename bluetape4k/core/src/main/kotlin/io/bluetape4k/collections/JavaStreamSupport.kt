package io.bluetape4k.collections

import java.util.*
import java.util.stream.DoubleStream
import java.util.stream.IntStream
import java.util.stream.LongStream
import java.util.stream.Stream
import java.util.stream.StreamSupport

fun <T> Stream<T>.asSequence(): Sequence<T> = Sequence { iterator() }
fun <T> Stream<T>.asIterable(): Iterable<T> = Iterable { iterator() }
fun <T> Stream<T>.toSet(): Set<T> = asIterable().toSet()

/**
 * [Stream]을 [Map] 으로 변환합니다.
 *
 * @param T  Stream 요소의 수형
 * @param K  Map 의 Key 수형
 * @param V  Map의 Value 수형
 * @param mapper Stream 요소를 Key와 Value로 변환하는 함수
 * @receiver Stream
 * @return Map<K, V> 인스턴스
 */
inline fun <T, K, V> Stream<T>.toMap(crossinline mapper: (item: T) -> Pair<K, V>): Map<K, V> =
    this.map { mapper(it) }.toList().toMap()

/**
 * [Stream]을 [MutableMap] 으로 변환합니다.
 *
 * @param T  Stream 요소의 수형
 * @param K  Map 의 Key 수형
 * @param V  Map의 Value 수형
 * @param mapper Stream 요소를 Key와 Value로 변환하는 함수
 * @receiver Stream
 * @return Map<K, V> 인스턴스
 */
inline fun <T, K, V> Stream<T>.toMutableMap(crossinline mapper: (item: T) -> Pair<K, V>): MutableMap<K, V> =
    this.toMap(mapper).toMutableMap()


fun <T> Iterator<T>.asStream(): Stream<T> {
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, 0), false)
}

fun <T> Iterable<T>.asStream(): Stream<T> = iterator().asStream()
fun <T> Sequence<T>.asStream(): Stream<T> = iterator().asStream()

fun <T> Iterator<T>.asParallelStream(): Stream<T> = asStream().parallel()
fun <T> Iterable<T>.asParallelStream(): Stream<T> = asStream().parallel()
fun <T> Sequence<T>.asParallelStream(): Stream<T> = asStream().parallel()

fun IntStream.asSequence(): Sequence<Int> = Sequence { iterator() }
fun IntStream.asIterable(): Iterable<Int> = Iterable { iterator() }
fun IntStream.toList(): List<Int> = asSequence().toList()
fun IntStream.toIntArray(): IntArray = asSequence().asIntArray()

fun Sequence<Int>.toIntStream(): IntStream = asStream().mapToInt { it }
fun Iterable<Int>.toIntStream(): IntStream = asStream().mapToInt { it }
fun IntArray.toIntStream(): IntStream = Arrays.stream(this)

fun LongStream.asSequence(): Sequence<Long> = Sequence { iterator() }
fun LongStream.asIterable(): Iterable<Long> = Iterable { iterator() }
fun LongStream.toList(): List<Long> = asSequence().toList()
fun LongStream.toLongArray(): LongArray = asSequence().asLongArray()

fun Sequence<Long>.toLongStream(): LongStream = asStream().mapToLong { it }
fun Iterable<Long>.toLongStream(): LongStream = asStream().mapToLong { it }
fun LongArray.toLongStream(): LongStream = Arrays.stream(this)

fun DoubleStream.asSequence(): Sequence<Double> = Sequence { iterator() }
fun DoubleStream.asIterable(): Iterable<Double> = Iterable { iterator() }
fun DoubleStream.toList(): List<Double> = asSequence().toList()
fun DoubleStream.toDoubleArray(): DoubleArray = asSequence().asDoubleArray()

fun Sequence<Double>.toDoubleStream(): DoubleStream = asStream().mapToDouble { it }
fun Iterable<Double>.toDoubleStream(): DoubleStream = asStream().mapToDouble { it }
fun DoubleArray.toDoubleStream(): DoubleStream = Arrays.stream(this)

/**
 * [FloatArray]를 [DoubleStream]으로 변환합니다.
 */
fun FloatArray.toDoubleStream(): DoubleStream {
    return DoubleStream.builder()
        .also { builder ->
            forEach { builder.add(it.toDouble()) }
        }
        .build()
}
