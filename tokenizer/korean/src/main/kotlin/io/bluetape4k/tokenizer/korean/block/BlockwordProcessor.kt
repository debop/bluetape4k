package io.bluetape4k.tokenizer.korean.block

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.error
import io.bluetape4k.logging.trace
import io.bluetape4k.support.EMPTY_STRING
import io.bluetape4k.tokenizer.exceptions.InvalidRequestException
import io.bluetape4k.tokenizer.korean.tokenizer.KoreanToken
import io.bluetape4k.tokenizer.korean.tokenizer.KoreanTokenizer
import io.bluetape4k.tokenizer.korean.utils.KoreanDictionaryProvider
import io.bluetape4k.tokenizer.korean.utils.KoreanPos
import io.bluetape4k.tokenizer.model.BlockwordRequest
import io.bluetape4k.tokenizer.model.BlockwordResponse
import io.bluetape4k.tokenizer.model.Severity
import java.util.Locale


/**
 * 한국어 금칙어를 Masking 처리합니다
 */
object BlockwordProcessor: KLogging() {

    private val blockedPos = listOf(
        KoreanPos.Noun,
        KoreanPos.Adjective,
        KoreanPos.Verb,
        KoreanPos.Adverb,
        KoreanPos.Korean,
        KoreanPos.KoreanParticle,
        KoreanPos.Foreign,
        KoreanPos.Number,
        KoreanPos.Alpha,        // 영어 금칙어도 적용한다
    )

    private val punctuationProcessor = PunctuationProcessor()

    /**
     * 금칙어 (Block words) 를 masking 합니다.
     *
     * 예: 미니미와 니미 -> 미니미와 **     // `니미` 는 속어
     *
     * @param request 금칙어 처리 요청 정보 [BlockwordRequest]
     * @return 금칙어를 처리한 결과 [BlockwordResponse]
     */
    fun maskBlockwords(request: BlockwordRequest): BlockwordResponse {
        if (request.text.isBlank()) {
            return BlockwordResponse(request, EMPTY_STRING)
        }
        if (request.options.locale.language != Locale.KOREAN.language) {
            throw InvalidRequestException("Invalid Locale. [${request.options.locale}]")
        }
        try {
            val punctuationRemoved = punctuationProcessor.removePunctuation(request.text)
            // val maskChunkText = maskChunk(punctuationRemoved, request.options.mask)
            val tokens = KoreanTokenizer.tokenize(punctuationRemoved)

            var result = punctuationRemoved
            val maskStr = request.options.mask
            val blockWords = mutableSetOf<String>()

            tokens.forEach { token ->
                log.trace { "token=$token" }
                if (canMask(token, request.options.severity)) {
                    log.trace { "mask token=$token" }
                    result =
                        result.replaceRange(token.offset, token.offset + token.length, maskStr.repeat(token.length))
                    blockWords.add(token.text)
                }
            }
            return BlockwordResponse(request, result, blockWords)
        } catch (e: Throwable) {
            log.error(e) { "Fail to mask block word. request=$request" }
            throw e
        }
    }

    //    private fun maskChunk(text: String, mask: String): String {
    //        val chunks = KoreanChunker.chunk(text)
    //
    //        var result = text
    //        chunks.forEach { chunk ->
    //            if (canMask(chunk)) {
    //                log.trace { "masked chunk=$chunk" }
    //                result = result.replaceRange(chunk.offset, chunk.offset + chunk.length, mask.repeat(chunk.length))
    //            }
    //        }
    //        log.trace { "mask chunk=$result" }
    //        return result
    //    }

    /**
     * [token]이 금칙어로서 mask 되어야 할 것인지 판단합니다.
     *
     * 단어 또는 동사의 기본형이 금칙어에 포함되어 있는지 검사한다
     */
    private fun canMask(token: KoreanToken, severity: Severity = Severity.DEFAULT): Boolean {
        return token.pos in blockedPos &&
            (
                KoreanDictionaryProvider.blockWords[severity]?.contains(token.text) ?: false ||
                    KoreanDictionaryProvider.blockWords[severity]?.contains(token.stem) ?: false
                )
    }
}
