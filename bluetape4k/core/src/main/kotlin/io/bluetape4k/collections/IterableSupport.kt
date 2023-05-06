package io.bluetape4k.collections

import java.util.*


fun <T> emptyIterator(): Iterator<T> = Collections.emptyIterator()
fun <T> emptyListIterator(): ListIterator<T> = Collections.emptyListIterator()

fun <T> Iterator<T>.asIterable(): Iterable<T> = Iterable { this }

fun <T> Iterator<T>.toList(): List<T> =
    arrayListOf<T>().apply { addAll(this@toList.asIterable()) }

fun <T> Iterator<T>.toMutableList(): MutableList<T> =
    arrayListOf<T>().apply { addAll(this@toMutableList.asIterable()) }

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

@JvmOverloads
fun Iterable<*>.asCharArray(dv: Char = '\u0000'): CharArray =
    map { runCatching { it.toString().first() }.getOrElse { dv } }.toCharArray()

@JvmOverloads
fun Iterable<*>.asByteArray(fallback: Byte = 0): ByteArray =
    map { runCatching { it.toString().toByte() }.getOrElse { fallback } }.toByteArray()

@JvmOverloads
fun Iterable<*>.asIntArray(fallback: Int = 0): IntArray =
    map { runCatching { it.toString().toInt() }.getOrElse { fallback } }.toIntArray()

@JvmOverloads
fun Iterable<*>.asLongArray(fallback: Long = 0): LongArray =
    map { runCatching { it.toString().toLong() }.getOrElse { fallback } }.toLongArray()

@JvmOverloads
fun Iterable<*>.asFloatArray(fallback: Float = 0.0F): FloatArray =
    map { runCatching { it.toString().toFloat() }.getOrElse { fallback } }.toFloatArray()

@JvmOverloads
fun Iterable<*>.asDoubleArray(fallback: Double = 0.0): DoubleArray =
    map { runCatching { it.toString().toDouble() }.getOrElse { fallback } }.toDoubleArray()

@JvmOverloads
fun Iterable<*>.asStringArray(fallback: String = ""): Array<String> =
    map { runCatching { it.toString() }.getOrElse { fallback } }.toTypedArray()

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
fun <T, R> Iterable<T>.sliding(size: Int, partialWindows: Boolean = true, transform: (List<T>) -> R): List<R> =
    windowed(size, 1, partialWindows, transform)
