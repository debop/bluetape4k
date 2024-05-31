package io.bluetape4k.support

import java.nio.ByteBuffer
import java.util.*


/**
 * [ByteArray]의 [count] 수만큼 앞에서부터 가져옵니다.
 *
 * @param count 가져올 갯수 ( 0 < count <= size)
 */
fun ByteArray.takeItems(count: Int): ByteArray = when {
    count <= 0 -> emptyByteArray
    else       -> this.copyOfRange(0, count.coerceAtMost(size))
}

/**
 * [ByteArray]의 [count] 수만큼 뒤에서부터 가져옵니다.
 *
 * @param count 가져올 갯수 (0 <= count <= size)
 */
fun ByteArray.dropItems(count: Int): ByteArray = when {
    count <= 0 -> this
    else       -> this.copyOfRange(count.coerceAtLeast(0), size)
}

/**
 * Int 값을 ByteArray로 변환합니다.
 */
fun Int.toByteArray(): ByteArray {
    return ByteBuffer.allocate(4).putInt(this).array()
}

/**
 * Long 값을 ByteArray로 변환합니다.
 */
fun Long.toByteArray(): ByteArray {
    return ByteBuffer.allocate(8).putLong(this).array()
}

/**
 * [UUID]를 ByteArray로 변환합니다.
 */
fun UUID.toByteArray(): ByteArray {
    return ByteBuffer
        .allocate(16)
        .putLong(this.mostSignificantBits)
        .putLong(this.leastSignificantBits)
        .array()
}

/**
 * ByteArray의 값을 Int로 변환합니다.
 */
fun ByteArray.toInt(offset: Int = 0): Int = ByteBuffer.wrap(this, offset, 4).int

/**
 * ByteArray의 값을 Long 으로 변환합니다.
 */
fun ByteArray.toLong(offset: Int = 0): Long = ByteBuffer.wrap(this, offset, 8).long

fun ByteArray.toUuid(offset: Int = 0): UUID {
    val buffer = ByteBuffer.wrap(this, offset, 16)
    return UUID(buffer.long, buffer.long)
}

fun ByteArray.indexOf(target: Byte, start: Int, end: Int): Int {
    start.requireZeroOrPositiveNumber("start")
    end.requireInRange(start, size, "end")

    for (i in start until end) {
        if (this[i] == target) {
            return i
        }
    }
    return -1
}

fun ByteArray.indexOf(target: ByteArray, start: Int = 0, end: Int = this.size): Int {
    start.requireZeroOrPositiveNumber("start")
    end.requireInRange(start, size, "end")

    if (target.isEmpty()) {
        return 0
    }

    outer@ for (i in start..<(end - target.size + 1)) {
        for (j in target.indices) {
            if (get(i + j) != target[j]) {
                continue@outer
            }
        }
        return i
    }
    return -1
}

fun ByteArray.lastIndexOf(target: Byte, start: Int, end: Int): Int {
    start.requireZeroOrPositiveNumber("start")
    end.requireInRange(start, size, "end")

    for (i in end - 1 downTo start) {
        if (this[i] == target) {
            return i
        }
    }
    return -1
}

/**
 * [ByteArray]의 크기를 늘려야 하는지 확인하고, 늘려야 한다면 [padding] 만큼 더 늘린 새로운 [ByteArray]를 반환합니다.
 *
 * @param minCapacity 최소 크기
 * @param padding     늘릴 크기
 * @return 새로게 생성된 [ByteArray] (기존 Array 의 값은 복사된다)
 */
fun ByteArray.ensureCapacity(minCapacity: Int, padding: Int): ByteArray {
    minCapacity.requireZeroOrPositiveNumber("minCapacity")
    padding.requireZeroOrPositiveNumber("padding")

    val self = this@ensureCapacity
    if (self.size >= minCapacity) {
        return self
    }

    return ByteArray(minCapacity + padding).apply {
        self.copyInto(this, 0)
    }
}

fun concat(vararg arrays: ByteArray): ByteArray {
    val totalSize = arrays.sumOf { it.size }
    val result = ByteArray(totalSize)
    var offset = 0
    for (array in arrays) {
        // System.arraycopy(array, 0, result, offset, array.size)
        array.copyInto(result, offset)
        offset += array.size
    }
    return result
}

fun ByteArray.reverseTo(fromIndex: Int = 0, toIndex: Int = size): ByteArray {
    fromIndex.requireZeroOrPositiveNumber("fromIndex")
    toIndex.requireInRange(fromIndex, size, "toIndex")

    val array = this@reverseTo
    return array.copyOf().apply {
        reverse(fromIndex, toIndex)
    }
}

fun ByteArray.reverse(fromIndex: Int = 0, toIndex: Int = size) {
    fromIndex.requireZeroOrPositiveNumber("fromIndex")
    toIndex.requireInRange(fromIndex, size, "toIndex")

    val array = this@reverse
    var i = fromIndex
    var j = toIndex - 1
    while (i < j) {
        val tmp = array[i]
        array[i] = array[j]
        array[j] = tmp
        i++
        j--
    }
}

fun ByteArray.rotateTo(distance: Int, fromIndex: Int = 0, toIndex: Int = size): ByteArray {
    fromIndex.requireZeroOrPositiveNumber("fromIndex")
    toIndex.requireInRange(fromIndex, size, "toIndex")

    val array = this@rotateTo
    val result = array.copyOf()
    result.rotate(distance, fromIndex, toIndex)
    return result
}

fun ByteArray.rotate(distance: Int, fromIndex: Int = 0, toIndex: Int = size) {
    fromIndex.requireZeroOrPositiveNumber("fromIndex")
    toIndex.requireInRange(fromIndex, size, "toIndex")

    val array = this@rotate

    if (array.size <= 1) {
        return
    }

    val length = toIndex - fromIndex
    // Obtain m = (-distance mod length), a non-negative value less than "length". This is how many
    // places left to rotate.
    var m = -distance % length
    m = if (m < 0) m + length else m

    // The current index of what will become the first element of the rotated section.
    val newFirstIndex = m + fromIndex
    if (newFirstIndex == fromIndex) {
        return
    }

    array.reverse(fromIndex, newFirstIndex)
    array.reverse(newFirstIndex, toIndex)
    array.reverse(fromIndex, toIndex)
}
