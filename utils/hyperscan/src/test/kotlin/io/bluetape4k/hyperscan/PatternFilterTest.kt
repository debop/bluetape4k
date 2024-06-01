package io.bluetape4k.hyperscan

import io.bluetape4k.junit5.concurrency.MultithreadingTester
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.utils.Runtimex
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.random.Random

class PatternFilterTest {

    companion object: KLogging()

    @Test
    fun `입력값이 첫번재 패턴에 필터링되는 경우`() {
        val patterns = listOf(
            Pattern.compile("The number is ([0-9]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("The color is (blue|red|orange)")
        )

        withPatternFilter(patterns) {
            val matchers = filter("The number is 7 the NUMber is 27")

            var matchrSize = 0
            var matchedCount = 0
            matchers.forEach { matcher ->
                matchrSize++
                while (matcher.find()) {
                    matchedCount++
                    log.debug { "Found match: ${matcher.group(1)}, [${matcher.regionStart()}, ${matcher.regionEnd()}]" }
                }
            }
            matchrSize shouldBeEqualTo 1
            matchedCount shouldBeEqualTo 2
        }
    }

    @Test
    fun `입력 값이 두번째 패턴에 필터링되는 경우`() {
        val patterns = listOf(
            Pattern.compile("The number is ([0-9]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("The color is (blue|red|orange)")
        )

        withPatternFilter(patterns) {
            val matchers = filter("The color is orange")
            assertHasPattern(patterns[1], matchers)
        }
    }

    @Test
    fun `UTF8 문자열에 대한 Filtering`() {
        val patterns = listOf(
            Pattern.compile("이 숫자는 ([0-9]+) 입니다", Pattern.CASE_INSENSITIVE),
            Pattern.compile("이 색상은 (파랑|빨강|오렌지)색입니다")
        )

        withPatternFilter(patterns) {
            val matchers = filter("이 숫자는 1234 입니다")
            assertHasPattern(patterns[0], matchers)
        }

        withPatternFilter(patterns) {
            val matchers = filter("이 색상은 빨강색입니다")
            assertHasPattern(patterns[1], matchers)
        }
    }

    @Test
    fun `Pattern flag 적용 예`() {
        val patterns = listOf(
            Pattern.compile("The number is ([0-9]+)"),
            Pattern.compile("The number is ([0-9]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^The color is (blue|red|orange)"),
            Pattern.compile("^The color is (blue|red|orange)", Pattern.MULTILINE),
            Pattern.compile("something.else"),
            Pattern.compile("something.else", Pattern.DOTALL),
            Pattern.compile("match.THIS", Pattern.CASE_INSENSITIVE or Pattern.DOTALL),
        )

        withPatternFilter(patterns) {
            // CASE_INSENSITIVE
            var matchers = filter("tHE nuMBeR is 17")
            assertHasPattern(patterns[1], matchers)
            matchers shouldHaveSize 1

            matchers = filter("The number is 17")
            assertHasPattern(patterns[0], matchers)
            assertHasPattern(patterns[1], matchers)
            matchers shouldHaveSize 2

            // MULTILINE & 문장 시작이 같은 경우
            matchers = filter("Some text\nThe color is red")
            assertHasPattern(patterns[3], matchers)
            matchers shouldHaveSize 1

            // DOTALL - 개행문자를 `.`으로 인식
            matchers = filter("something\nelse")
            assertHasPattern(patterns[5], matchers)
            matchers shouldHaveSize 1

            // DOTALL - 개행문자를 `.`으로 인식, CASE_INSENSITIVE
            matchers = filter("match\nthis")
            assertHasPattern(patterns[6], matchers)
            matchers shouldHaveSize 1
        }
    }

    private fun assertHasPattern(pattern: Pattern, matchers: List<Matcher>) {
        val filteredPatterns = matchers.map { it.pattern() }
        filteredPatterns shouldContain pattern
    }

    @Test
    fun `pattern filter in multi-threading`() {
        val patterns = listOf(
            Pattern.compile("The number is ([0-9]+)"),
            Pattern.compile("The number is ([0-9]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^The color is (blue|red|orange)"),
            Pattern.compile("^The color is (blue|red|orange)", Pattern.MULTILINE),
            Pattern.compile("something.else"),
            Pattern.compile("something.else", Pattern.DOTALL),
            Pattern.compile("match.THIS", Pattern.CASE_INSENSITIVE or Pattern.DOTALL),
        )

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
                // PatternFilter 는 Thread-safe 하지 않음
                withPatternFilter(patterns) {
                    val matchers = filter(texts[Random.nextInt(texts.size)]).toList()
                    matchers.size shouldBeGreaterThan 0
                }
            }
            .run()
    }
}
