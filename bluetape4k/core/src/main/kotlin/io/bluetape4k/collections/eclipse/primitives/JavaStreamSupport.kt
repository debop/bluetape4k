package io.bluetape4k.collections.eclipse.primitives

import io.bluetape4k.collections.eclipse.fastListOf
import io.bluetape4k.collections.eclipse.unifiedSetOf
import io.bluetape4k.collections.stream.asIterable
import org.eclipse.collections.api.DoubleIterable
import org.eclipse.collections.api.FloatIterable
import org.eclipse.collections.api.IntIterable
import org.eclipse.collections.api.LongIterable
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList
import org.eclipse.collections.impl.list.mutable.primitive.FloatArrayList
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList
import java.util.stream.DoubleStream
import java.util.stream.IntStream
import java.util.stream.LongStream
import java.util.stream.Stream

fun <T> Stream<T>.toFastList() = fastListOf(asIterable())
fun <T> Stream<T>.toUnifiedSet() = unifiedSetOf(asIterable())


fun IntStream.toIntArrayList(): IntArrayList = intArrayListOf(asIterable())
fun IntIterable.toIntStream(): IntStream = IntStream.builder()
    .also { builder ->
        forEach { builder.accept(it) }
    }
    .build()

fun LongStream.toLongArrayList(): LongArrayList = longArrayListOf(asIterable())
fun LongIterable.toLongStream(): LongStream = LongStream.builder()
    .also { builder ->
        forEach { builder.accept(it) }
    }
    .build()

fun DoubleStream.toDoubleArrayList(): DoubleArrayList = doubleArrayListOf(asIterable())
fun DoubleIterable.toDoubleStream(): DoubleStream = DoubleStream.builder()
    .also { builder ->
        forEach { builder.accept(it) }
    }
    .build()

fun DoubleStream.toFloatArrayList(): FloatArrayList = floatArrayListOf(asIterable().map { it.toFloat() })
fun FloatIterable.toDoubleStream(): DoubleStream = DoubleStream.builder()
    .also { builder ->
        forEach { builder.accept(it.toDouble()) }
    }
    .build()
