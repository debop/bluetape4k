package io.bluetape4k.tokenizer.korean


val Char.isSpaceChar: Boolean get() = Character.isSpaceChar(this)

/**
 * 마지막 요소만 제거한 문자열을 반환합니다.
 */
fun String.init(): String = if (isEmpty()) "" else dropLast(1)

/**
 * 마지막 요소만 제거한 문자열을 반환합니다.
 */
fun CharSequence.init(): CharSequence = if (isEmpty()) "" else dropLast(1)

/**
 * 마지막 요소만 제거한 문자열을 반환합니다.
 */
fun <T> List<T>.init(): List<T> = if (isEmpty()) emptyList() else dropLast(1)
