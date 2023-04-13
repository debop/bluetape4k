package io.bluetape4k.collections.stream

import io.bluetape4k.collections.asDoubleArray
import io.bluetape4k.collections.asIntArray
import io.bluetape4k.collections.asLongArray
import io.bluetape4k.collections.eclipse.fastListOf
import org.eclipse.collections.impl.list.mutable.FastList
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList
import org.eclipse.collections.impl.list.mutable.primitive.FloatArrayList
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList
import java.util.*
import java.util.stream.DoubleStream
import java.util.stream.IntStream
import java.util.stream.LongStream
import java.util.stream.Stream
import kotlin.streams.toList


fun <T> Stream<T>.asSequence(): Sequence<T> = Sequence { iterator() }
fun <T> Stream<T>.asIterable(): Iterable<T> = Iterable { iterator() }

//fun <T> Stream<T>.toList(): List<T> = asIterable().toList()
fun <T> Stream<T>.toFastList(): FastList<T> = fastListOf(asIterable())
fun <T> Stream<T>.toSet(): Set<T> = asIterable().toSet()


inline fun <T, K, V> Stream<T>.toMap(crossinline mapper: (item: T) -> Pair<K, V>): Map<K, V> =
    this.map { mapper(it) }.toList().toMap()

inline fun <T, K, V> Stream<T>.toMutableMap(crossinline mapper: (item: T) -> Pair<K, V>): MutableMap<K, V> =
    this.toMap(mapper).toMutableMap()


fun <T> Iterator<T>.asStream(): Stream<T> {
    val builder = Stream.builder<T>()
    while (hasNext()) {
        builder.accept(next())
    }
    return builder.build()
}

fun <T> Iterable<T>.asStream(): Stream<T> = iterator().asStream()
fun <T> Sequence<T>.asStream(): Stream<T> = iterator().asStream()

fun <T> Iterator<T>.asParallelStream(): Stream<T> = asStream().parallel()
fun <T> Iterable<T>.asParallelStream(): Stream<T> = iterator().asStream().parallel()
fun <T> Sequence<T>.asParallelStream(): Stream<T> = iterator().asStream().parallel()


fun IntStream.asSequence(): Sequence<Int> = Sequence { iterator() }
fun IntStream.asIterable(): Iterable<Int> = Iterable { iterator() }
fun IntStream.toList(): List<Int> = asIterable().toList()
fun IntStream.toIntArray(): IntArray = asIterable().asIntArray()

fun Sequence<Int>.toIntStream(): IntStream = asIterable().asStream().mapToInt { it }
fun Iterable<Int>.toIntStream(): IntStream = asStream().mapToInt { it }
fun IntArray.toIntStream(): IntStream = Arrays.stream(this)
fun IntArrayList.toIntStream(): IntStream =
    IntStream.builder()
        .also { builder ->
            forEach { builder.accept(it) }
        }
        .build()


fun LongStream.asSequence(): Sequence<Long> = Sequence { iterator() }
fun LongStream.asIterable(): Iterable<Long> = Iterable { iterator() }
fun LongStream.toList(): List<Long> = asIterable().toList()
fun LongStream.toLongArray(): LongArray = asIterable().asLongArray()

fun Sequence<Long>.toLongStream(): LongStream = asIterable().asStream().mapToLong { it }
fun Iterable<Long>.toLongStream(): LongStream = asStream().mapToLong { it }
fun LongArray.toLongStream(): LongStream = Arrays.stream(this)

fun LongArrayList.toLongStream(): LongStream =
    LongStream.builder()
        .also { builder ->
            forEach { builder.accept(it) }
        }
        .build()

fun DoubleStream.asSequence(): Sequence<Double> = Sequence { iterator() }
fun DoubleStream.asIterable(): Iterable<Double> = Iterable { iterator() }
fun DoubleStream.toList(): List<Double> = asIterable().toList()
fun DoubleStream.toDoubleArray(): DoubleArray = asIterable().asDoubleArray()


/**
 * `Sequence[Double]`을 [DoubleStream]으로 변환합니다.
 */
fun Sequence<Double>.toDoubleStream(): DoubleStream = asIterable().asStream().mapToDouble { it }

/**
 * `Iterable<Double>`를 [DoubleStream]으로 변환합니다.
 */
fun Iterable<Double>.toDoubleStream(): DoubleStream = asStream().mapToDouble { it }

/**
 * [DoubleArray]를 [DoubleStream]으로 변환합니다.
 */
fun DoubleArray.toDoubleStream(): DoubleStream = Arrays.stream(this)

/**
 * [DoubleArrayList]를 [DoubleStream]으로 변환합니다.
 */
fun DoubleArrayList.toDoubleStream(): DoubleStream =
    DoubleStream.builder()
        .also { builder ->
            forEach { builder.accept(it) }
        }
        .build()

/**
 * [FloatArray]를 [DoubleStream]으로 변환합니다.
 */
fun FloatArray.toDoubleStream(): DoubleStream =
    DoubleStream.builder()
        .also { builder ->
            forEach { builder.accept(it.toDouble()) }
        }
        .build()

/**
 * [FloatArrayList]를 [DoubleStream]으로 변환합니다.
 */
fun FloatArrayList.toDoubleStream(): DoubleStream =
    DoubleStream.builder()
        .also { builder ->
            forEach { builder.accept(it.toDouble()) }
        }
        .build()
