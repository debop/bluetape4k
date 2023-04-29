package io.bluetape4k.collections.eclipse.ranges

import io.bluetape4k.core.assertPositiveNumber
import java.util.stream.IntStream
import java.util.stream.LongStream
import org.eclipse.collections.impl.list.mutable.primitive.CharArrayList
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList
import kotlin.math.sign

fun charProgressionOf(start: Char, endInclusive: Char, step: Int = 1): CharProgression =
    CharProgression.fromClosedRange(start, endInclusive, step)

fun CharProgression.toCharArrayList(): CharArrayList {
    val array = CharArrayList(this.count())
    this.forEachIndexed { index, value ->
        array[index] = value
    }
    return array
}

fun intProgressionOf(start: Int, endInclusive: Int, step: Int = 1): IntProgression =
    IntProgression.fromClosedRange(start, endInclusive, step)

fun IntProgression.toIntArrayList(): IntArrayList {
    val array = IntArrayList(this.count())
    this.forEachIndexed { index, value ->
        array[index] = value
    }
    return array
}

fun IntProgression.asStream(): IntStream {
    val builder = IntStream.builder()
    forEach { builder.add(it) }
    return builder.build()
}

fun IntProgression.grouped(groupSize: Int): Sequence<IntProgression> {
    groupSize.assertPositiveNumber("groupSize")

    val size = count()
    val partitionCount = size / groupSize + (if (size % groupSize == 0) 0 else 1)

    return partitioning(partitionCount)
}

fun IntProgression.partitioning(partitionCount: Int = 1): Sequence<IntProgression> = sequence {
    partitionCount.assertPositiveNumber("partitionCount")

    val step = this@partitioning.step
    val count = this@partitioning.count()
    val stepSign = step.sign
    val partitionSize = count / partitionCount
    var remainder = count % partitionCount

    var start = this@partitioning.first
    repeat(partitionCount) {
        var endInclusive = start + (partitionSize + (if (remainder > 0) 1 else 0)) * stepSign
        if (remainder > 0) {
            remainder--
        }
        endInclusive = when {
            step > 0 -> minOf(endInclusive, this@partitioning.last - 1 * stepSign)
            else -> maxOf(endInclusive, this@partitioning.last - 1 * stepSign)
        }
        endInclusive += stepSign

        val partition = IntProgression.fromClosedRange(start, endInclusive, step)
        yield(partition)
        start = endInclusive + 1 * stepSign
    }
}

fun longProgressionOf(start: Long, endInclusive: Long, step: Long = 1L): LongProgression =
    LongProgression.fromClosedRange(start, endInclusive, step)

fun LongProgression.toLongArrayList(): LongArrayList {
    val array = LongArrayList(this.count())
    this.forEachIndexed { index, value ->
        array[index] = value
    }
    return array
}

fun LongProgression.asStream(): LongStream {
    val builder = LongStream.builder()
    forEach { builder.add(it) }
    return builder.build()
}

fun LongProgression.grouped(groupSize: Int): Sequence<LongProgression> {
    groupSize.assertPositiveNumber("groupSize")

    val size = count()
    val partitionCount = size / groupSize + (if (size % groupSize == 0) 0 else 1)

    return partitioning(partitionCount)
}

fun LongProgression.partitioning(partitionCount: Int = 1): Sequence<LongProgression> = sequence {
    partitionCount.assertPositiveNumber("partitionCount")

    val step = this@partitioning.step
    val count = this@partitioning.count()
    val stepSign = step.sign
    val partitionSize = count / partitionCount
    var remainder = count % partitionCount

    var start = this@partitioning.first
    repeat(partitionCount) {
        var endInclusive = start + (partitionSize + (if (remainder > 0) 1 else 0)) * stepSign
        if (remainder > 0) {
            remainder--
        }
        endInclusive = when {
            step > 0 -> minOf(endInclusive, this@partitioning.last - 1 * stepSign)
            else -> maxOf(endInclusive, this@partitioning.last - 1 * stepSign)
        }
        endInclusive += stepSign

        val partition = LongProgression.fromClosedRange(start, endInclusive, step)
        yield(partition)
        start = endInclusive + 1 * stepSign
    }
}
