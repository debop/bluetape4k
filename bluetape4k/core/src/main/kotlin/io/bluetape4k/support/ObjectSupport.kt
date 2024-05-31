package io.bluetape4k.support

import java.util.*


/**
 * 가장 빈번하게 발생하는 항목을 찾습니다.
 */
fun <T: Any> modeOrNull(vararg items: T): T? = items.asSequence().modeOrNull()

/**
 * 가장 빈번하게 발생하는 항목을 찾습니다.
 */
fun <T: Any> Iterable<T>.modeOrNull(): T? = asSequence().modeOrNull()

/**
 * 가장 빈번하게 발생하는 항목을 찾습니다.
 */
fun <T: Any> Sequence<T>.modeOrNull(): T? {
    val occurrences = HashMap<T, Int>()

    this.forEach {
        val count = occurrences[it]
        if (count == null) {
            occurrences[it] = 1
        } else {
            occurrences[it] = count + 1
        }
    }
    var result: T? = null
    var max = 0
    occurrences.forEach { (key, value) ->
        if (value == max) {
            result = null
        } else if (value > max) {
            max = value
            result = key
        }
    }
    return result
}

/**
 * Find the "best guess" middle value among comparables. If there is an even
 * number of total values, the lower of the two middle values will be returned.
 *
 * @receiver collection of values to process
 * @param <T> type of values processed by this method
 * @return T at middle position
 */
inline fun <reified T: Comparable<T>> Collection<T>.median(): T? {
    this.requireNotEmpty("median")
    val sort = TreeSet<T>()
    Collections.addAll(sort, *this.toTypedArray())
    return sort.toArray()[(sort.size - 1) / 2] as T
}

/**
 * Find the "best guess" middle value among comparables. If there is an even
 * number of total values, the lower of the two middle values will be returned.
 *
 * @receiver collection of values to process
 * @param <T> type of values processed by this method
 * @return T at middle position
 */
inline fun <reified T> Collection<T>.median(comparator: Comparator<T>): T? {
    this.requireNotEmpty("median")
    val sort = TreeSet(comparator)
    Collections.addAll(sort, *this.toTypedArray())
    return sort.toArray()[(sort.size - 1) / 2] as T
}
