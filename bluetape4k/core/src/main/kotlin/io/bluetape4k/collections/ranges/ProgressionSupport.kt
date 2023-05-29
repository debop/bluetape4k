package io.bluetape4k.collections.ranges

import io.bluetape4k.core.assertPositiveNumber
import java.util.stream.IntStream
import java.util.stream.LongStream

fun charProgressionOf(start: Char, endInclusive: Char, step: Int = 1): CharProgression =
    CharProgression.fromClosedRange(start, endInclusive, step)

fun intProgressionOf(start: Int, endInclusive: Int, step: Int = 1): IntProgression =
    IntProgression.fromClosedRange(start, endInclusive, step)

fun IntProgression.asStream(): IntStream =
    IntStream.builder()
        .also { builder ->
            forEach { builder.add(it) }
        }
        .build()

fun IntProgression.grouped(groupSize: Int): Sequence<IntProgression> {
    groupSize.assertPositiveNumber("groupSize")

    val size = count()
    val partitionCount = size / groupSize + (if (size % groupSize > 0) 1 else 0)

    return partitioning(partitionCount)
}

fun IntProgression.partitioning(partitionCount: Int = 1): Sequence<IntProgression> = sequence {
    partitionCount.assertPositiveNumber("partitionCount")
    val self = this@partitioning

    if (partitionCount == 1) {
        yield(self)
        return@sequence
    }

    val step = self.step
    val count = self.count()
    val reminder = count % partitionCount
    val partitionSize = count / partitionCount + (if (reminder > 0) 1 else 0)

    var start = self.first
    repeat(partitionCount) {
        var endInclusive = start
        repeat(partitionSize - 1) {
            endInclusive += self.step
        }
        endInclusive = when {
            step > 0 -> endInclusive.coerceAtMost(self.last)
            else     -> endInclusive.coerceAtLeast(self.last)
        }

        val partition = IntProgression.fromClosedRange(start, endInclusive, step)
        yield(partition)
        start = endInclusive + step
    }
}

fun longProgressionOf(start: Long, endInclusive: Long, step: Long = 1L): LongProgression =
    LongProgression.fromClosedRange(start, endInclusive, step)


fun LongProgression.asStream(): LongStream {
    return LongStream.builder()
        .also { builder ->
            forEach { builder.add(it) }
        }
        .build()
}

fun LongProgression.grouped(groupSize: Int): Sequence<LongProgression> {
    groupSize.assertPositiveNumber("groupSize")

    val size = count()
    val partitionCount = size / groupSize + (if (size % groupSize > 0) 1 else 0)

    return partitioning(partitionCount)
}

fun LongProgression.partitioning(partitionCount: Int = 1): Sequence<LongProgression> = sequence {
    partitionCount.assertPositiveNumber("partitionCount")
    val self = this@partitioning

    if (partitionCount == 1) {
        yield(self)
        return@sequence
    }

    val step = self.step
    val count = self.count()
    val reminder = count % partitionCount
    val partitionSize = count / partitionCount + (if (reminder > 0) 1 else 0)

    var start = self.first
    repeat(partitionCount) {
        var endInclusive = start
        repeat(partitionSize - 1) {
            endInclusive += self.step
        }
        endInclusive = when {
            step > 0 -> endInclusive.coerceAtMost(self.last)
            else     -> endInclusive.coerceAtLeast(self.last)
        }

        val partition = LongProgression.fromClosedRange(start, endInclusive, step)
        yield(partition)
        start = endInclusive + step
    }
}
