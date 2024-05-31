package io.bluetape4k.support

/**
 * [StringBuilder] 에 [Iterable] 을 추가합니다.
 *
 * @param T
 * @param iterable
 * @param separator
 */
fun <T> StringBuilder.appendItems(iterable: Iterable<T>, separator: String = ", ") {
    appendItems(iterable.asSequence(), separator)
}

/**
 * [StringBuilder] 에 [Sequence] 를 추가합니다.
 *
 * @param T
 * @param sequence
 * @param separator
 */
fun <T> StringBuilder.appendItems(sequence: Sequence<T>, separator: String = ", ") {
    val iter = sequence.iterator()
    if (iter.hasNext()) {
        append(iter.next())
    }
    while (iter.hasNext()) {
        append(separator)
        append(iter.next())
    }
}
