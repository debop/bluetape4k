package io.bluetape4k.hyperscan

import io.bluetape4k.logging.KLogging

/**
 * [Regex] 컬렉션을 이용하여, 입력값이 매칭되는지 파악합니다.
 *
 * @property regexs 검색할 정규식 컬렉션
 */
class RegexFilter(
    val regexs: List<Regex>,
) {
    companion object: KLogging() {
        @JvmField
        val DEFAULT_REGEX_OPTIONS = setOf(
            RegexOption.IGNORE_CASE,
            RegexOption.MULTILINE,
            RegexOption.DOT_MATCHES_ALL
        )

        @JvmStatic
        operator fun invoke(
            regexprs: Collection<String>,
            regexOptions: Set<RegexOption> = DEFAULT_REGEX_OPTIONS,
        ): RegexFilter {
            val regexes = regexprs.filter { it.isNotBlank() }.map { it.toRegex(regexOptions) }
            return RegexFilter(regexes)
        }
    }

    /**
     * 정규식[regexs] 중 [input]과 매칭되는 결과를 모두 반환한다.
     *
     * @param input 검사할 문자열
     * @return 매칭 결과 컬렉션
     */
    fun findAll(input: String): List<MatchResult> {
        if (input.isBlank()) {
            return emptyList()
        }
        return regexs.flatMap { it.findAll(input) }
    }

    /**
     * 정규식[regexs] 중 [input]과 매칭되는 첫번째 결과를 반환한다.
     *
     * @param input 검사할 문자열
     * @return 첫번째 매칭된 결과 또는 null
     */
    fun findFirst(input: String): MatchResult? {
        if (input.isBlank()) {
            return null
        }
        return regexs.firstNotNullOfOrNull { it.find(input) }
    }
}
