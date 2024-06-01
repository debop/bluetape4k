package io.bluetape4k.hyperscan

import io.bluetape4k.junit5.concurrency.MultithreadingTester
import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.Runtimex
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldHaveSize
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test
import kotlin.random.Random

class RegexFilterTest {

    companion object: KLogging()

    @Test
    fun `입력값이 첫번재 Regex에 필터링되는 경우`() {
        val regexs = listOf(
            "The number is ([0-9]+)".toRegex(RegexOption.IGNORE_CASE),
            "The color is (blue|red|orange)".toRegex()
        )

        val regexFilter = RegexFilter(regexs)
        val matchResults = regexFilter.findAll("The number is 7 the NUMber is 27")
        matchResults shouldHaveSize 2
    }

    @Test
    fun `입력값이 두번재 Regex에 필터링되는 경우`() {
        val regexs = listOf(
            "The number is ([0-9]+)".toRegex(RegexOption.IGNORE_CASE),
            "The color is (blue|red|orange)".toRegex()
        )

        val regexFilter = RegexFilter(regexs)
        val matchResults = regexFilter.findAll("The color is orange")
        matchResults shouldHaveSize 1

        // 대소문자 구분이 있다 
        regexFilter.findAll("The COLOR is ORANGE").shouldBeEmpty()
    }

    @Test
    fun `UTF8 문자열에 대한 Filtering`() {
        val regexs = listOf(
            "이 숫자는 ([0-9]+)입니다".toRegex(RegexOption.IGNORE_CASE),
            "이 색상은 (파랑|빨강|오렌지)색입니다".toRegex()
        )
        val regexFilter = RegexFilter(regexs)

        val results = regexFilter.findAll("이 숫자는 1234입니다")
        results shouldHaveSize 1

        val results2 = regexFilter.findAll("이 색상은 빨강색입니다")
        results2 shouldHaveSize 1
    }

    @Test
    fun `RegexOption 적용 예`() {
        val regexs = listOf(
            "The number is ([0-9]+)".toRegex(),
            "The number is ([0-9]+)".toRegex(RegexOption.IGNORE_CASE),
            "^The color is (blue|red|orange)".toRegex(),
            "^The color is (blue|red|orange)".toRegex(RegexOption.MULTILINE),
            "something.else".toRegex(),
            "something.else".toRegex(RegexOption.DOT_MATCHES_ALL),
            "match.THIS".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)),
        )

        val regexFilter = RegexFilter(regexs)

        // CASE_INSENSITIVE
        var results = regexFilter.findAll("tHE nuMBeR is 17")
        results shouldHaveSize 1

        results = regexFilter.findAll("The number is 17")
        results shouldHaveSize 2

        // MULTILINE & 문장 시작이 같은 경우
        results = regexFilter.findAll("Some text\nThe color is red")
        results shouldHaveSize 1

        // DOTALL - 개행문자를 `.`으로 인식
        results = regexFilter.findAll("something\nelse")
        results shouldHaveSize 1

        // DOTALL - 개행문자를 `.`으로 인식, CASE_INSENSITIVE
        results = regexFilter.findAll("match\nthis")
        results shouldHaveSize 1
    }

    @Test
    fun `정규식 검색 in multi-threading`() {
        val regexs = listOf(
            "The number is ([0-9]+)".toRegex(),
            "The number is ([0-9]+)".toRegex(RegexOption.IGNORE_CASE),
            "^The color is (blue|red|orange)".toRegex(),
            "^The color is (blue|red|orange)".toRegex(RegexOption.MULTILINE),
            "something.else".toRegex(),
            "something.else".toRegex(RegexOption.DOT_MATCHES_ALL),
            "match.THIS".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)),
        )

        val regexFilter = RegexFilter(regexs)

        val texts = listOf(
            "tHE nuMBeR is 17",
            "The number is 17",
            "Some text\nThe color is red",
            "something\nelse",
            "match\nthis"
        )

        MultithreadingTester()
            .numThreads(2 * Runtimex.availableProcessors)
            .roundsPerThread(10)
            .add {
                val results = regexFilter.findAll(texts[Random.nextInt(texts.size)]).toList()
                results.shouldNotBeEmpty()
            }
            .run()
    }
}
