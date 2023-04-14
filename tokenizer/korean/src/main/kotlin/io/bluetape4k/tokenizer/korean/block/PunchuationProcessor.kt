package io.bluetape4k.tokenizer.korean.block

import io.bluetape4k.collections.sliding
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import io.bluetape4k.tokenizer.korean.tokenizer.KoreanChunker
import io.bluetape4k.tokenizer.korean.tokenizer.KoreanToken
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.CashTag
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Email
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Foreign
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Hashtag
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Korean
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.KoreanParticle
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Number
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Punctuation


/**
 * 금칙어를 회피하기 위해 구두점(Punctuation)을 단어 중간에 추가하는 회피방식에 대응하기 위해, 구두점(Punctuation) 을 제거하도록 합니다.
 *
 * ```
 * 섹.스 -> 섹스
 * 찌~찌~뽕 -> 찌찌뽕
 * ```
 *
 */
class PunctuationProcessor {

    companion object: KLogging() {
        private val normalPos = arrayOf(Korean, Foreign, Number, KoreanParticle)
        private val punctuationPos = arrayOf(Punctuation, Email, Hashtag, CashTag)
    }

    fun removePunctuation(text: String): String {
        val tokens = findPunctuation(text)
        var result = text
        tokens.reversed()
            .forEach {
                val token = it.first
                log.trace { "remove token. $it" }
                if (it.second) {
                    result = result.removeRange(token.offset, token.offset + token.length)
                }
            }
        return result
    }

    fun findPunctuation(text: String): List<Pair<KoreanToken, Boolean>> {
        val chunks = KoreanChunker.chunk(text)

        return chunks.sliding(3, false)
            .mapIndexed { index, tokens ->
                log.trace { "sliding tokens=$tokens" }
                (index + 1) to canRemovePunctuation(tokens)
            }
            .toList()
            .map { chunks[it.first] to it.second }
    }


    private fun canRemovePunctuation(tokens: List<KoreanToken>): Boolean {
        if (tokens.size < 3) {
            return false
        }
        val prev = tokens[0]
        val current = tokens[1]
        val next = tokens[2]

        return prev.pos in normalPos &&
            current.pos in punctuationPos &&
            next.pos in normalPos
    }
}
