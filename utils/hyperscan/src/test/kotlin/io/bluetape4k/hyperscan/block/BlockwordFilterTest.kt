package io.bluetape4k.hyperscan.block

import io.bluetape4k.junit5.concurrency.MultithreadingTester
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.utils.Runtimex
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNullOrBlank
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class BlockwordFilterTest {

    companion object: KLogging()

    private val blockwordFilter = BlockwordFilter()

    @ParameterizedTest
    @CsvSource(
        value = [
            "너는 바보인가? ㅅㅂ 웃기네:ㅅㅂ",
            "너는 바보인가? ㅅㅂㅅㅋ:ㅅㅂㅅㅋ",
            "빙신 스키같으니...:빙신",
            "오늘 룸~싸롱가자:룸~싸롱",
            "중국 애들을 짱.깨라고 하지:짱.깨",
            "느~금.마 바보:느~금.마",
            "부엉이 거기바위:부엉이 거기바위",
            "이런 말은 하지마, 노무현 ~ 운지:노무현 ~ 운지",
            "너는 퐁퐁~넘이야:퐁퐁~넘",
            "새로 차 사서 카.쎅:카.쎅",
            "2.항문^^섹쑤:항문^^섹",
            "느.금~마는 뭐지?:느.금~마",
        ], delimiter = ':'
    )
    fun `금칙어가 들어간 문장에서 금칙어 추출하기`(text: String, expected: String) {
        log.debug { "text=$text, expected=$expected" }

        val blockwords = blockwordFilter.filter(text)
        log.debug { "block words=${blockwords.joinToString()}" }
        blockwords shouldContain expected.trim()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "너는 바보인가? 메롱",
            "너는 바보인가? 쓰바쓰바",
        ]
    )
    fun `금칙어가 없는 문장에 대한 통과`(text: String) {
        log.debug { "text=$text" }

        val blockwords = blockwordFilter.filter(text)
        blockwords.shouldBeEmpty()
    }

    @Test
    fun `금칙어 필터 - Hyperscan - Multi threading`() {
        val text = "느.금~마는 뭐지?, 중국 애들을 짱.깨라고 하지"

        MultithreadingTester()
            .numThreads(2 * Runtimex.availableProcessors)
            .roundsPerThread(100)
            .add {
                blockwordFilter.filter(text).shouldNotBeEmpty()
            }
            .run()
    }

    @Test
    fun `금칙어 필터 - Kotlin Regex - Multi threading`() {
        val text = "느.금~마는 뭐지?, 중국 애들을 짱.깨라고 하지"

        MultithreadingTester()
            .numThreads(2 * Runtimex.availableProcessors)
            .roundsPerThread(100)
            .add {
                val blockwords = filterBlockwords(text)
                blockwords.shouldNotBeNullOrBlank()
            }
            .run()
    }

    val regexs = BlockwordPatternProvider.blockPatterns.map { it.toRegex() }

    private fun filterBlockwords(text: String): String? {
        return regexs.firstNotNullOfOrNull { regex ->
            regex.find(text)?.value
        }.apply {
            log.debug { "Found=$this" }
        }
    }

    @Test
    fun `filterFirst - 첫번째 금칙어 적발 시 반환`() {
        val text = "느.금~마는 뭐지?, 중국 애들을 짱.깨라고 하지"

        MultithreadingTester()
            .numThreads(2 * Runtimex.availableProcessors)
            .roundsPerThread(100)
            .add {
                blockwordFilter.filterFirst(text).shouldNotBeNullOrBlank()
            }
            .run()
    }

    @Test
    fun `filterAll - 모든 금칙어 적발`() {
        val text = "느.금~마는 뭐지?, 중국 애들을 짱.깨라고 하지"

        MultithreadingTester()
            .numThreads(2 * Runtimex.availableProcessors)
            .roundsPerThread(100)
            .add {
                blockwordFilter.filterAll(text).shouldNotBeEmpty()
            }
            .run()
    }
}
