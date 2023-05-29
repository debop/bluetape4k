package io.bluetape4k.collections

import io.bluetape4k.support.asByte
import io.bluetape4k.support.asChar
import io.bluetape4k.support.asDouble
import io.bluetape4k.support.asFloat
import io.bluetape4k.support.asInt
import io.bluetape4k.support.asLong
import io.bluetape4k.support.asString
import java.util.*


fun <T> emptyIterator(): Iterator<T> = Collections.emptyIterator()
fun <T> emptyListIterator(): ListIterator<T> = Collections.emptyListIterator()

fun <T> Iterator<T>.asIterable(): Iterable<T> = Iterable { this }

fun <T> Iterator<T>.toList(): List<T> =
    mutableListOf<T>().apply { addAll(this@toList.asIterable()) }

fun <T> Iterator<T>.toMutableList(): MutableList<T> =
    mutableListOf<T>().apply { addAll(this@toMutableList.asIterable()) }

fun <T> Iterable<T>.size(): Int = when (this) {
    is Collection<T> -> this.size
    else             -> count()
}

inline fun <T> Iterable<T>.exists(predicate: (T) -> Boolean): Boolean = any { predicate(it) }

/** Iterable이 다른 iterable과 같은 요소들을 가졌는가? */
infix fun <T> Iterable<T>.isSameElements(that: Iterable<T>): Boolean {
    if (this is List<T> && that is List<T>) {
        if (this.size == that.size) {
            return this.indices.all { this[it] == that[it] }
        }
        return false
    }

    val left = this.iterator()
    val right = that.iterator()
    while (left.hasNext() && right.hasNext()) {
        if (left.next() != right.next()) {
            return false
        }
    }
    return !(left.hasNext() || right.hasNext())
}

fun Iterable<*>.asCharArray(dv: Char = '\u0000'): CharArray =
    map { it.asChar(dv) }.toCharArray()

fun Iterable<*>.asByteArray(fallback: Byte = 0): ByteArray =
    map { it.asByte(fallback) }.toByteArray()

fun Iterable<*>.asIntArray(fallback: Int = 0): IntArray =
    map { it.asInt(fallback) }.toIntArray()

fun Iterable<*>.asLongArray(fallback: Long = 0): LongArray =
    map { it.asLong(fallback) }.toLongArray()

fun Iterable<*>.asFloatArray(fallback: Float = 0.0F): FloatArray =
    map { it.asFloat(fallback) }.toFloatArray()

fun Iterable<*>.asDoubleArray(fallback: Double = 0.0): DoubleArray =
    map { it.asDouble(fallback) }.toDoubleArray()

fun Iterable<*>.asStringArray(fallback: String = ""): Array<String> =
    map { it.asString(fallback) }.toTypedArray()

inline fun <reified T: Any> Iterable<*>.asArray(): Array<T?> =
    map { it as? T }.toTypedArray()

/**
 * [mapper] 실행의 [Result] 를 반환합니다.
 *
 * @param mapper 변환 작업
 * @return
 */
inline fun <T, R> Iterable<T>.tryMap(mapper: (T) -> R): List<Result<R>> =
    map { runCatching { mapper(it) } }

/**
 * [mapper] 실행이 성공한 결과만 추출합니다.
 *
 * @param mapper 변환 작업
 * @return
 */
inline fun <T, R: Any> Iterable<T>.mapIfSuccess(mapper: (T) -> R): List<R> =
    mapNotNull { runCatching { mapper(it) }.getOrNull() }

inline fun <T> Iterable<T>.tryForEach(action: (T) -> Unit) {
    forEach { runCatching { action(it) } }
}

inline fun <T, R> Iterable<T>.mapCatching(mapper: (T) -> R): List<Result<R>> =
    map { runCatching { mapper(it) } }

inline fun <T> Iterable<T>.forEachCatching(action: (T) -> Unit): List<Result<Unit>> {
    return map { runCatching { action(it) } }
}

/**
 * 컬렉션의 요소를 [size]만큼의 켤렉션으로 묶어서 반환합니다. 마지막 켤렉션의 크기는 [size]보다 작을 수 있습니다.
 *
 * @param size Sliding 요소의 수
 * @return Sliding 된 요소를 담은 컬렉션
 */
fun <T> Iterable<T>.sliding(size: Int, partialWindows: Boolean = true): List<List<T>> =
    windowed(size, 1, partialWindows)

/**
 * 컬렉션의 요소를 [size]만큼의 켤렉션으로 묶은 것을 [transform]으로 변환하여 반환합니다.
 *
 * @param size Sliding 요소의 수
 * @param transform 변환 함수
 * @return Sliding 된 요소를 변환한 컬렉션
 */
inline fun <T, R> Iterable<T>.sliding(
    size: Int,
    partialWindows: Boolean = true,
    crossinline transform: (List<T>) -> R,
): List<R> =
    windowed(size, 1, partialWindows) { transform(it) }
