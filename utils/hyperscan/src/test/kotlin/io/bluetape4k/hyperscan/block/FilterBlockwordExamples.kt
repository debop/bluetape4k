package io.bluetape4k.hyperscan.block

import io.bluetape4k.hyperscan.PatternFilter
import io.bluetape4k.hyperscan.patternFilterOf
import io.bluetape4k.hyperscan.withPatternFilter
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.util.regex.Pattern

class FilterBlockwordExamples {

    companion object: KLogging()

    @Test
    fun `욕설 금칙어 필터링`() {
        val patterns = listOf(
            Pattern.compile("(^|['\"]*)ㅅㅂ(ㅅㄲ|새끼|련|년)*"),
            Pattern.compile("(^|['\"]*)ㅆㅂ(ㅅㄲ|새끼|련|년)*"),
            Pattern.compile("병신(ㅅㄲ|새끼|련|년|놈)*([ .,!?]|\$)")
        )

        filterBlockword("ㅅㅂ ㅈ같네 ㅅㅂㅅㄲ ㅅㅂ새끼 ㅆㅂ련 빙신 바보병신놈", patterns, 5) // ㅈ같네는 패턴에 없음
    }

    @Test
    fun `약물 관련 금칙어 필터링`() {
        val patterns = listOf(
            Pattern.compile("(^|['\"]*)대마(초|초잎|잎|��)")
        )
        patternFilterOf(patterns).use { patternFilter ->
            patternFilter.blockword("대마초 대마초잎 대마잎 대마�� 대구빡", 4)
            patternFilter.blockword("대모초 다이마추", 0)
        }
    }

    private fun filterBlockword(text: String, patterns: List<Pattern>, expectedCount: Int) {
        log.debug { "Filter blockword. text=$text" }

        withPatternFilter(patterns) {
            this.blockword(text, expectedCount)
        }
    }

    private fun PatternFilter.blockword(text: String, expectedCount: Int) {
        val matchers = filter(text)  // ㅈ같네는 패턴에 없음

        var matchedCount = 0
        matchers.forEach { matcher ->
            while (matcher.find()) {
                matchedCount++
                val matchResult = matcher.toMatchResult()
                log.debug { "Found match=${matchResult.group().trim()}" }
            }
        }
        matchedCount shouldBeEqualTo expectedCount
    }

    @Test
    fun `정규식으로 금칙어 추출하기 예제`() {
        val patterns = listOf(
            Pattern.compile("(^|['\"]*)룸(싸|사|쌀|살|)롱"),
            Pattern.compile("(^|['\"]*)롱타임")
        )

        withPatternFilter(patterns) {
            val matchers = filter("룸사롱 롱싸롱 룸살롱").toList()
            matchers.forEach { matcher ->
                while (matcher.find()) {
                    val matchResult = matcher.toMatchResult()
                    log.debug { "Found match=${matchResult.group().trim()}" }
                }
            }
        }
    }
}
