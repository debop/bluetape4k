package io.bluetape4k.collections


@JvmOverloads
fun charSequence(start: Char, endInclusive: Char, step: Int = 1): Sequence<Char> =
    CharProgression.fromClosedRange(start, endInclusive, step).asSequence()

@JvmOverloads
fun byteSequence(start: Byte, endInclusive: Byte, step: Byte = 1): Sequence<Byte> = sequence {
    var current = start
    while (current <= endInclusive) {
        yield(current)
        current = (current + step).toByte()
    }
}

@JvmOverloads
fun intSequence(start: Int, endInclusive: Int, step: Int = 1): Sequence<Int> =
    IntProgression.fromClosedRange(start, endInclusive, step).asSequence()

@JvmOverloads
fun longSequence(start: Long, endInclusive: Long, step: Long = 1L): Sequence<Long> =
    LongProgression.fromClosedRange(start, endInclusive, step).asSequence()

@JvmOverloads
fun floatSequence(start: Float, endInclusive: Float, step: Float = 1.0F): Sequence<Float> = sequence {
    var current = start
    while (current <= endInclusive) {
        yield(current)
        current += step
    }
}

@JvmOverloads
fun doubleSequence(start: Double, endInclusive: Double, step: Double = 1.0): Sequence<Double> = sequence {
    var current = start
    while (current <= endInclusive) {
        yield(current)
        current += step
    }
}

fun Sequence<Char>.toCharArray(): CharArray =
    CharArray(count()).also { array ->
        this.forEachIndexed { i, v ->
            array[i] = v
        }
    }

fun Sequence<Byte>.toByteArray(): ByteArray =
    ByteArray(count()).also { array ->
        this.forEachIndexed { i, v ->
            array[i] = v
        }
    }


fun Sequence<Short>.toShortArray(): ShortArray =
    ShortArray(count()).also { array ->
        this.forEachIndexed { i, v ->
            array[i] = v
        }
    }

fun Sequence<Int>.toIntArray(): IntArray =
    IntArray(count()).also { array ->
        this.forEachIndexed { i, v ->
            array[i] = v
        }
    }


fun Sequence<Long>.toLongArray(): LongArray =
    LongArray(count()).also { array ->
        this.forEachIndexed { i, v ->
            array[i] = v
        }
    }

fun Sequence<Float>.toFloatArray(): FloatArray =
    FloatArray(count()).also { array ->
        this.forEachIndexed { i, v ->
            array[i] = v
        }
    }

fun Sequence<Double>.toDoubleArray(): DoubleArray =
    DoubleArray(count()).also { array ->
        this.forEachIndexed { i, v ->
            array[i] = v
        }
    }


/**
 * 컬렉션의 요소를 [size]만큼의 켤렉션으로 묶어서 반환합니다. 마지막 켤렉션의 크기는 [size]보다 작을 수 있습니다.
 *
 * @param size Sliding 요소의 수
 * @return Sliding 된 요소를 담은 컬렉션
 */
fun <T> Sequence<T>.sliding(size: Int, partialWindows: Boolean = true): List<List<T>> =
    asIterable().windowed(size, 1, partialWindows)

/**
 * 컬렉션의 요소를 [size]만큼의 켤렉션으로 묶은 것을 [transform]으로 변환하여 반환합니다.
 *
 * @param size Sliding 요소의 수
 * @param transform 변환 함수
 * @return Sliding 된 요소를 변환한 컬렉션
 */
fun <T, R> Sequence<T>.sliding(size: Int, partialWindows: Boolean = true, transform: (List<T>) -> R): List<R> =
    asIterable().windowed(size, 1, partialWindows, transform)