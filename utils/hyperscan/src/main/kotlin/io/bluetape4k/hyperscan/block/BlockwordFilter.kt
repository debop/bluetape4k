package io.bluetape4k.hyperscan.block

import io.bluetape4k.hyperscan.withPatternFilter
import io.bluetape4k.logging.KLogging

/**
 * 문자열에서 금칙어에 해당하는 부분을 정규식으로 추출하는 필터입니다.
 *
 * ```
 * val words = BlockwordFilter().filter("너는 바보인가? ㅅㅂ새끼")  // [ㅅㅂ새끼]
 * ```
 */
class BlockwordFilter {

    companion object: KLogging()

    /**
     * Hyperscan을 이용하여 금칙어에 해당하는 문자열을 필터링하여 제공합니다.
     *
     * **NOTE: Hyperscan 은 필터에 해당하는 정규식이 많을 수로 급속도로 성능이 느려진다.**
     *
     * @param text 검사할 문자열 (eg. 너는 바보인가? ㅅㅂ)
     * @return 금칙어에 해당하는 문자열 컬렉션 (eg. [ㅅㅂ])
     */
    fun filter(text: String): List<String> {
        return withPatternFilter(BlockwordPatternProvider.blockPatterns) {
            filter(text).mapNotNull { matcher ->
                if (matcher.find()) {
                    matcher.toMatchResult().group().trim()
                } else {
                    null
                }
            }
        }
    }

    /**
     * Regex를 사용하여 첫 번째로 발견된 금칙어를 반환합니다.
     *
     * @param text
     * @return
     */
    fun filterFirst(text: String): String? {
        return BlockwordPatternProvider.blockRegex
            .firstNotNullOfOrNull { regex -> regex.find(text)?.value?.trim() }
    }

    /**
     * Regex를 사용하여 모든 금칙어를 반환합니다.
     *
     * @param text
     * @return
     */
    fun filterAll(text: String): List<String> {
        return BlockwordPatternProvider.blockRegex
            .flatMap { regex -> regex.findAll(text).map { it.value }.toList() }
    }
}
