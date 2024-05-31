package io.bluetape4k.support

fun DoubleArray.indexOf(target: Double, start: Int, end: Int): Int {
    start.requireZeroOrPositiveNumber("start")
    end.requireInRange(start, size, "end")

    for (i in start until end) {
        if (this[i] == target) {
            return i
        }
    }
    return -1
}

fun DoubleArray.indexOf(target: DoubleArray, start: Int = 0, end: Int = this.size): Int {
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

fun DoubleArray.lastIndexOf(target: Double, start: Int = 0, end: Int = this@lastIndexOf.size): Int {
    start.requireZeroOrPositiveNumber("start")
    end.requireInRange(start, size, "end")

    this.min()
    for (i in end - 1 downTo start) {
        if (this[i] == target) {
            return i
        }
    }
    return -1

}

fun DoubleArray.ensureCapacity(minCapacity: Int, padding: Int): DoubleArray {
    minCapacity.requireZeroOrPositiveNumber("minCapacity")
    padding.requireZeroOrPositiveNumber("padding")

    val self = this@ensureCapacity
    if (self.size >= minCapacity) {
        return self
    }

    val newCapacity = minCapacity + padding
    return DoubleArray(newCapacity).apply {
        self.copyInto(this, 0)
    }
}

fun concat(vararg arrays: DoubleArray): DoubleArray {
    val totalLength = arrays.sumOf { it.size }
    val result = DoubleArray(totalLength)
    var offset = 0
    for (array in arrays) {
        array.copyInto(result, offset)
        offset += array.size
    }
    return result
}

fun DoubleArray.reverseTo(fromIndex: Int, toIndex: Int): DoubleArray {
    fromIndex.requireInRange(0, size - 1, "fromIndex")
    toIndex.requireInRange(fromIndex, size, "toIndex")

    return this@reverseTo.copyOf().apply {
        reverse(fromIndex, toIndex)
    }
}

fun DoubleArray.reverse(fromIndex: Int = 0, toIndex: Int = size) {
    fromIndex.requireInRange(0, size - 1, "fromIndex")
    toIndex.requireInRange(fromIndex, size, "toIndex")

    var i = fromIndex
    var j = toIndex - 1
    while (i < j) {
        val temp = this[i]
        this[i] = this[j]
        this[j] = temp
        i++
        j--
    }
}

fun DoubleArray.rotateTo(distance: Int, fromIndex: Int = 0, toIndex: Int = size): DoubleArray {
    fromIndex.requireInRange(0, size - 1, "fromIndex")
    toIndex.requireInRange(fromIndex, size, "toIndex")

    return this@rotateTo.copyOf().apply {
        rotate(distance, fromIndex, toIndex)
    }
}

fun DoubleArray.rotate(distance: Int, fromIndex: Int = 0, toIndex: Int = size) {
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
