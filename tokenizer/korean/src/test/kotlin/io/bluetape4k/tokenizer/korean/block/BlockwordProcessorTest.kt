package io.bluetape4k.tokenizer.korean.block

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.tokenizer.korean.KoreanProcessor
import io.bluetape4k.tokenizer.model.BlockwordOptions
import io.bluetape4k.tokenizer.model.BlockwordRequest
import io.bluetape4k.tokenizer.model.Severity
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

class BlockwordProcessorTest {

    companion object: KLogging() {
        const val REPEAT_SIZE = 5
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `금칙어 회피 시도를 처리한다`() {
        // `걸.레` 는 resources/koreantext/block/block_low.txt 에 이미 등록되어 있다
        KoreanProcessor.addBlockwords(listOf("걸레"), Severity.LOW)

        val original = "홈쇼핑 미니미는 무슨 걸.레 어쩌라구?"
        val expected = "홈쇼핑 미니미는 무슨 ** 어쩌라구?"
        val request = BlockwordRequest(original)
        val response = BlockwordProcessor.maskBlockwords(request)

        log.debug { "maskedText=$response" }
        response.maskedText shouldBeEqualTo expected
        response.blockWords shouldContainSame setOf("걸레")
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `동적으로 금칙어를 추가하면 처리합니다`() {
        // `걸.레` 는 resources/koreantext/block/block_low.txt 에 이미 등록되어 있다
        KoreanProcessor.addBlockwords(listOf("걸레", "찌찌뽕", "대끼리"), Severity.MIDDLE)

        val original = "홈쇼핑 미니미는 무슨 걸.레야 어쩌라구? 찌찌뽕이야? 대끼리야?"
        val expected = "홈쇼핑 미니미는 무슨 **야 어쩌라구? ***이야? ***야?"
        val request = BlockwordRequest(original, BlockwordOptions(severity = Severity.MIDDLE))
        val response = BlockwordProcessor.maskBlockwords(request)

        log.debug { "response=$response" }
        response.maskedText shouldBeEqualTo expected
        response.blockWords shouldContainAll arrayOf("걸레", "찌찌뽕", "대끼리")
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `심각도가 HIGH 인 금칙어는 금칙어 처리 요청 시 LOW 라면 처리되지 않습니다`() {
        KoreanProcessor.addBlockwords(listOf("찌찌뽕", "대끼리"), Severity.MIDDLE)
        KoreanProcessor.addBlockwords(listOf("히로뽕"), Severity.HIGH)

        val original = "홈쇼핑 미니미는 무슨 히.로뽕이야 어쩌라구? 찌.찌뽕이야? 대.끼리야? 심각도 HIGH"
        val expected = "홈쇼핑 미니미는 무슨 ***이야 어쩌라구? 찌찌뽕이야? 대끼리야? 심각도 HIGH"
        val request = BlockwordRequest(original, BlockwordOptions(severity = Severity.LOW))
        val response = BlockwordProcessor.maskBlockwords(request)

        log.debug { "response=$response" }
        response.maskedText shouldBeEqualTo expected
        response.blockWords shouldContainAll arrayOf("히로뽕") // Severity LOW 인 금칙어는 처리하지 않는다
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `동사 처리`() {
        KoreanProcessor.addBlockwords(listOf("싸다", "분수쑈"), Severity.LOW)

        val original = "홈쇼핑 분수쑈 쌀.거같아 쌀거같아요 싼다고"
        val expected = "홈쇼핑 *** **같아 **같아요 ***"
        val request = BlockwordRequest(original, BlockwordOptions(severity = Severity.LOW))
        val response = BlockwordProcessor.maskBlockwords(request)

        log.debug { "response=$response" }
        response.maskedText shouldBeEqualTo expected
        response.blockWords shouldContainAll arrayOf("분수쑈", "쌀거")
    }

    @Test
    fun `복합 신조어`() {
        KoreanProcessor.addBlockwords(listOf("3초찍", "삼초찍"), Severity.LOW)

        val original = "복합 신조어 3초/찍 삼초/찍"
        val expected = "복합 신조어 3초찍 ***"
        val request = BlockwordRequest(original, BlockwordOptions(severity = Severity.LOW))
        val response = BlockwordProcessor.maskBlockwords(request)

        log.debug { "response=$response" }
        response.maskedText shouldBeEqualTo expected
        response.blockWords shouldContainAll arrayOf("삼초찍")
    }

    @Test
    fun `영어 금칙어 적용`() {
        KoreanProcessor.addBlockwords(listOf("fuck", "sex"), Severity.LOW)

        val original = "영어 fuck 이 적용되나? 그럼 sexy는?"
        val expected = "영어 **** 이 적용되나? 그럼 sexy는?"
        val request = BlockwordRequest(original, BlockwordOptions(severity = Severity.LOW))
        val response = BlockwordProcessor.maskBlockwords(request)

        log.debug { "response=$response" }
        response.maskedText shouldBeEqualTo expected
        response.blockWords shouldContainAll arrayOf("fuck")
    }
}
