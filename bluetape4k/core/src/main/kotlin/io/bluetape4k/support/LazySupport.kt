package io.bluetape4k.support

/**
 * [initializer]가 동시에 실행되어도, 마지막으로 실행된 값을 제공한다.
 *
 * @param initializer 값을 제공하는 함수
 * @return [Lazy] 인스턴스
 */
fun <T> unsafeLazy(initializer: () -> T): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE, initializer)

/**
 * [initializer]가 동시에 실행되어도, 첫번째 실행된 값을 제공한다.
 *
 * @param initializer 값을 제공하는 함수
 * @return [Lazy] 인스턴스
 */
fun <T> publicLazy(initializer: () -> T): Lazy<T> =
    lazy(LazyThreadSafetyMode.PUBLICATION, initializer)
