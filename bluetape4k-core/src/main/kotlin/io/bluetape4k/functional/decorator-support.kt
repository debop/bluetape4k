package io.bluetape4k.functional

/**
 * Decorator 타입을 나타냅니다.
 */
typealias Decorator<T> = (() -> T) -> T // (T) -> T

/**
 * 일반 함수에 대해 decorator pattern 을 적용합니다.
 *
 * ```
 * fun italic(f:() -> String): String = "<i>${f()}</i>"
 * fun hello() = decorateWith(::italic) { "hello" }        // return <i>hello</i>
 * ```
 *
 * @param T
 * @param decorators
 * @param action
 * @receiver
 * @return
 */
fun <T> decorateWith(vararg decorators: Decorator<T>, action: () -> T): T {
    return decorators.fold(initial = action()) { acc, decorator ->
        decorator { acc }
    }
}
