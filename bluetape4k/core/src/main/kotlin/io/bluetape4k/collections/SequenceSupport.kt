package io.bluetape4k.collections

import io.bluetape4k.core.requireLe
import io.bluetape4k.support.asByte
import io.bluetape4k.support.asChar
import io.bluetape4k.support.asDouble
import io.bluetape4k.support.asFloat
import io.bluetape4k.support.asInt
import io.bluetape4k.support.asLong
import io.bluetape4k.support.asString

fun charSequenceOf(start: Char, endInclusive: Char, step: Int = 1): Sequence<Char> {
    start.requireLe(endInclusive, "start")
    return CharProgression.fromClosedRange(start, endInclusive, step).asSequence()
}

fun byteSequenceOf(start: Byte, endInclusive: Byte, step: Byte = 1): Sequence<Byte> = sequence {
    start.requireLe(endInclusive, "start")

    var current = start
    while (current <= endInclusive) {
        yield(current)
        current = (current + step).toByte()
    }
}

fun intSequenceOf(start: Int, endInclusive: Int, step: Int = 1): Sequence<Int> {
    start.requireLe(endInclusive, "start")
    return IntProgression.fromClosedRange(start, endInclusive, step).asSequence()
}

fun longSequenceOf(start: Long, endInclusive: Long, step: Long = 1L): Sequence<Long> {
    start.requireLe(endInclusive, "start")
    return LongProgression.fromClosedRange(start, endInclusive, step).asSequence()
}

fun floatSequenceOf(start: Float, endInclusive: Float, step: Float = 1.0F): Sequence<Float> = sequence {
    start.requireLe(endInclusive, "start")

    var current = start
    while (current <= endInclusive) {
        yield(current)
        current += step
    }
}

fun doubleSequenceOf(start: Double, endInclusive: Double, step: Double = 1.0): Sequence<Double> = sequence {
    start.requireLe(endInclusive, "start")
    var current = start
    while (current <= endInclusive) {
        yield(current)
        current += step
    }
}

fun Sequence<Char>.toCharArray(): CharArray = toList().toCharArray()
fun Sequence<Byte>.toByteArray(): ByteArray = toList().toByteArray()
fun Sequence<Short>.toShortArray(): ShortArray = toList().toShortArray()
fun Sequence<Int>.toIntArray(): IntArray = toList().toIntArray()
fun Sequence<Long>.toLongArray(): LongArray = toList().toLongArray()
fun Sequence<Float>.toFloatArray(): FloatArray = toList().toFloatArray()
fun Sequence<Double>.toDoubleArray(): DoubleArray = toList().toDoubleArray()

fun Sequence<*>.asCharArray(dv: Char = '\u0000'): CharArray =
    map { it.asChar(dv) }.toCharArray()

fun Sequence<*>.asByteArray(fallback: Byte = 0): ByteArray =
    map { it.asByte(fallback) }.toByteArray()

fun Sequence<*>.asIntArray(fallback: Int = 0): IntArray =
    map { it.asInt(fallback) }.toIntArray()

fun Sequence<*>.asLongArray(fallback: Long = 0): LongArray =
    map { it.asLong(fallback) }.toLongArray()

fun Sequence<*>.asFloatArray(fallback: Float = 0.0F): FloatArray =
    map { it.asFloat(fallback) }.toFloatArray()

fun Sequence<*>.asDoubleArray(fallback: Double = 0.0): DoubleArray =
    map { it.asDouble(fallback) }.toDoubleArray()

fun Sequence<*>.asStringArray(fallback: String = ""): Array<String> =
    map { it.asString(fallback) }.toList().toTypedArray()

inline fun <reified T: Any> Sequence<*>.asArray(): Array<T?> =
    map { it as? T }.toList().toTypedArray()


/**
 * [mapper] 실행의 [Result] 를 반환합니다.
 *
 * @param mapper 변환 작업
 * @return
 */
inline fun <T, R> Sequence<T>.tryMap(crossinline mapper: (T) -> R): Sequence<Result<R>> =
    map { runCatching { mapper(it) } }

/**
 * [mapper] 실행이 성공한 결과만 추출합니다.
 *
 * @param mapper 변환 작업
 * @return
 */
inline fun <T, R: Any> Sequence<T>.mapIfSuccess(crossinline mapper: (T) -> R): Sequence<R> =
    mapNotNull { runCatching { mapper(it) }.getOrNull() }

inline fun <T> Sequence<T>.tryForEach(action: (T) -> Unit) {
    forEach { runCatching { action(it) } }
}

inline fun <T, R> Sequence<T>.mapCatching(crossinline mapper: (T) -> R): Sequence<Result<R>> =
    map { runCatching { mapper(it) } }

inline fun <T> Sequence<T>.forEachCatching(crossinline action: (T) -> Unit): Sequence<Result<Unit>> {
    return map { runCatching { action(it) } }
}


/**
 * 컬렉션의 요소를 [size]만큼의 켤렉션으로 묶어서 반환합니다. 마지막 켤렉션의 크기는 [size]보다 작을 수 있습니다.
 *
 * @param size Sliding 요소의 수
 * @return Sliding 된 요소를 담은 컬렉션
 */
fun <T> Sequence<T>.sliding(size: Int, partialWindows: Boolean = true): Sequence<List<T>> =
    windowed(size, 1, partialWindows)

/**
 * 컬렉션의 요소를 [size]만큼의 켤렉션으로 묶은 것을 [transform]으로 변환하여 반환합니다.
 *
 * @param size Sliding 요소의 수
 * @param transform 변환 함수
 * @return Sliding 된 요소를 변환한 컬렉션
 */
inline fun <T, R> Sequence<T>.sliding(
    size: Int,
    partialWindows: Boolean = true,
    crossinline transform: (List<T>) -> R,
): Sequence<R> =
    windowed(size, 1, partialWindows) { transform(it) }
