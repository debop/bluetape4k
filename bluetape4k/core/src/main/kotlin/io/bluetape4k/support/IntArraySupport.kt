package io.bluetape4k.support


fun IntArray.indexOf(target: Int, start: Int, end: Int): Int {
    start.requireZeroOrPositiveNumber("start")
    end.requireInRange(start, size, "end")

    for (i in start until end) {
        if (this[i] == target) {
            return i
        }
    }
    return -1
}

fun IntArray.indexOf(target: IntArray, start: Int = 0, end: Int = this.size): Int {
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

fun IntArray.lastIndexOf(target: Int, start: Int = 0, end: Int = this@lastIndexOf.size): Int {
    start.requireZeroOrPositiveNumber("start")
    end.requireInRange(start, size, "end")

    for (i in end - 1 downTo start) {
        if (this[i] == target) {
            return i
        }
    }
    return -1

}

fun IntArray.ensureCapacity(minCapacity: Int, padding: Int): IntArray {
    minCapacity.requireZeroOrPositiveNumber("minCapacity")
    padding.requireZeroOrPositiveNumber("padding")

    val self = this@ensureCapacity
    if (self.size >= minCapacity) {
        return self
    }

    val newCapacity = minCapacity + padding
    return IntArray(newCapacity).apply {
        self.copyInto(this, 0)
    }
}

fun concat(vararg arrays: IntArray): IntArray {
    val totalLength = arrays.sumOf { it.size }
    val result = IntArray(totalLength)
    var offset = 0
    for (array in arrays) {
        array.copyInto(result, offset)
        offset += array.size
    }
    return result
}

fun IntArray.reverseTo(fromIndex: Int, toIndex: Int): IntArray {
    fromIndex.requireInRange(0, size - 1, "fromIndex")
    toIndex.requireInRange(fromIndex, size, "toIndex")

    return this@reverseTo.copyOf().apply {
        reverse(fromIndex, toIndex)
    }
}

fun IntArray.reverse(fromIndex: Int = 0, toIndex: Int = size) {
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

fun IntArray.rotateTo(distance: Int, fromIndex: Int = 0, toIndex: Int = size): IntArray {
    fromIndex.requireInRange(0, size - 1, "fromIndex")
    toIndex.requireInRange(fromIndex, size, "toIndex")

    return this@rotateTo.copyOf().apply {
        rotate(distance, fromIndex, toIndex)
    }
}

fun IntArray.rotate(distance: Int, fromIndex: Int = 0, toIndex: Int = size) {
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
