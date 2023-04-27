package io.bluetape4k.data.cassandra

import com.datastax.oss.driver.internal.core.util.Strings

private const val DOUBLE_SINGLE_QUOTE = "\'\'"
private const val SINGLE_QUOTE = "\'"

/**
 * String 컬럼에 문자열을 저장할 때 single quotation (`'`)이 있다면 중복해주도록 합니다 (-> `''`)
 *
 * ```
 * Simpson's family -> 'Simpson''s family'
 * ```
 */
//fun Any?.quote(): String? =
//    this?.toString()
//        ?.replace(SINGLE_QUOTE, DOUBLE_SINGLE_QUOTE)
//        ?.let { SINGLE_QUOTE + it + SINGLE_QUOTE }


/**
 * String 컬럼에 문자열을 저장할 때 single quotation (`'`)이 있다면 중복해주도록 합니다 (-> `''`)
 *
 * ```
 * Simpson's family -> 'Simpson''s family'
 * null -> ''  // NOTE: Cassandra 에는 null 이라는 게 없다. 그냥 빈문자열이다
 * ```
 */
fun Any?.quote(): String = Strings.quote(this?.toString())

fun Any?.doubleQuote(): String = Strings.doubleQuote(this?.toString())

fun String.unquote(): String = Strings.unquote(this)

fun String.unDoubleQuote(): String = Strings.unDoubleQuote(this)

fun String?.isQuoted(): Boolean = Strings.isQuoted(this)

fun String?.isDoubleQuoted(): Boolean = Strings.isDoubleQuoted(this)

fun String.needsDoubleQuotes(): Boolean = isNotEmpty() && Strings.needsDoubleQuotes(this)
