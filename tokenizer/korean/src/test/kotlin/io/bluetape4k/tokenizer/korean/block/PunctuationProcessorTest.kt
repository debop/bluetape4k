package io.bluetape4k.tokenizer.korean.block

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest

class PunctuationProcessorTest {

    companion object: KLogging()

    private val punctuationProcessor = PunctuationProcessor()

    @RepeatedTest(10)
    fun `중간 punctuation 제거하기`() {
        val actual = "중.@.고등#학교에서... 너는 뭐_했니? 난_1학년^^ 확.성기 섹.스 찌~~찌~~뽕"
        val punctuationRemoved = "중고등학교에서... 너는 뭐했니? 난1학년^^ 확성기 섹스 찌찌뽕"

        val removePunctuation = punctuationProcessor.removePunctuation(actual)
        log.debug { "remove punctuation=$removePunctuation" }
        removePunctuation shouldBeEqualTo punctuationRemoved
    }
}
