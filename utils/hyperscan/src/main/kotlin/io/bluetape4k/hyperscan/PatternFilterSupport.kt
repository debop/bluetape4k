package io.bluetape4k.hyperscan

import java.util.regex.Matcher
import java.util.regex.Pattern


fun patternFilterOf(pattern: Pattern, vararg patterns: Pattern): PatternFilter {
    return PatternFilter(listOf(pattern, *patterns))
}

fun patternFilterOf(patterns: List<Pattern>): PatternFilter {
    return PatternFilter(patterns)
}

/**
 * [PatternFilter] 를 사용하여 [input] 에서 필터링된 [Matcher] 를 반환한다.
 *
 * ```
 * val patterns = listOf(
 *     Pattern.compile("이 숫자는 ([0-9]+) 입니다", Pattern.CASE_INSENSITIVE),
 *     Pattern.compile("이 색상은 (파랑|빨강|오렌지)색입니다")
 * )
 *
 * withPatternFilter(patterns) {
 *     val matchers = filter("이 숫자는 1234 입니다")
 *     assertHasPattern(patterns[0], matchers)
 * }
 * ```
 *
 * @param T
 * @param pattern
 * @param funcBody
 * @receiver
 * @return
 */
inline fun <T> withPatternFilter(pattern: Pattern, funcBody: PatternFilter.() -> T): T {
    return patternFilterOf(pattern).use { filter ->
        funcBody(filter)
    }
}

inline fun <T> withPatternFilter(patterns: List<Pattern>, funcBody: PatternFilter.() -> T): T {
    return patternFilterOf(patterns).use { filter ->
        funcBody(filter)
    }
}
