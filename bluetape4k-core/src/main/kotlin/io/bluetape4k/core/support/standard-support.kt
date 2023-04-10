package io.bluetape4k.core.support

/**
 * 모든 인자가 not null 일 때에만 [block] 을 실행합니다.
 */
inline fun <T1 : Any, T2 : Any, R : Any> safeLet(
    p1: T1?,
    p2: T2?,
    block: (T1, T2) -> R?
): R? {
    return if (p1 != null && p2 != null)
        block(p1, p2)
    else null
}

/**
 * 모든 인자가 not null 일 때에만 [block] 을 실행합니다.
 */
inline fun <T1 : Any, T2 : Any, T3 : Any, R : Any> safeLet(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    block: (T1, T2, T3) -> R?
): R? {
    return if (p1 != null && p2 != null && p3 != null)
        block(p1, p2, p3)
    else null
}

/**
 * 모든 인자가 not null 일 때에만 [block] 을 실행합니다.
 */
inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, R : Any> safeLet(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    p4: T4?,
    block: (T1, T2, T3, T4) -> R?
): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null)
        block(p1, p2, p3, p4)
    else null
}

/**
 * 모든 인자가 not null 일 때에만 [block] 을 실행합니다.
 */
inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, R : Any> safeLet(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    p4: T4?,
    p5: T5?,
    block: (T1, T2, T3, T4, T5) -> R?
): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null && p5 != null)
        block(p1, p2, p3, p4, p5)
    else null
}


/**
 * [options] 의 모든 요소가 not null 일 때 [block] 을 수행합니다.
 */
inline fun <T : Any, R> whenAllNotNull(vararg options: T?, block: (List<T>) -> R) {
    if (options.all { it != null }) {
        block(options.filterNotNull())
    }
}

/**
 * [options] 의 not null 요소가 하나라도 있을 때, not null 인 요소 만으로 [block] 을 수행합니다.
 */
inline fun <T : Any, R> whenAnyNotNull(vararg options: T?, block: (List<T>) -> R) {
    if (options.any { it != null }) {
        block(options.filterNotNull())
    }
}

/**
 * [options] 의 모든 요소가 not null 일 때 [block] 을 수행합니다.
 */
fun <T : Any, R> Iterable<T?>.whenAllNotNull(block: (List<T>) -> R) {
    if (all { it != null }) {
        block(this.filterNotNull())
    }
}

/**
 * [options] 의 not null 요소가 하나라도 있을 때, not null 인 요소 만으로 [block] 을 수행합니다.
 */
fun <T : Any, R> Iterable<T?>.whenAnyNotNull(block: (List<T>) -> R) {
    if (any { it != null }) {
        block(this.filterNotNull())
    }
}

/**
 * [options] 중 첫번째 not null 요소를 반환합니다. 모두 null 이면 null을 반환합니다.
 */
fun <T : Any> coalesce(vararg options: T?): T? = options.firstOrNull { it != null }

/**
 * [Iterable] 요소 not null 인 첫 번째 요소를 반환합니다. 모두 null 이면 null을 반환합니다.
 */
fun <T : Any> Iterable<T?>.coalesce(): T? = firstOrNull { it != null }
