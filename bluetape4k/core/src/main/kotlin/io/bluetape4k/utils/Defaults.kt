package io.bluetape4k.utils

inline fun <reified T> defaultValue(): T {
    return when (T::class) {
        Boolean::class -> false
        Byte::class    -> 0.toByte()
        Short::class   -> 0.toShort()
        Int::class     -> 0
        Long::class    -> 0L
        Float::class   -> 0.0f
        Double::class  -> 0.0
        Char::class    -> 0.toChar()
        else           -> null
    } as T
}
