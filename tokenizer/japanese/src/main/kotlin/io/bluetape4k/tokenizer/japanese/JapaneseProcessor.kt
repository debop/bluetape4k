package io.bluetape4k.tokenizer.japanese

import com.atilika.kuromoji.ipadic.Token
import io.bluetape4k.tokenizer.japanese.block.JapaneseBlockwordProcessor
import io.bluetape4k.tokenizer.japanese.tokenizer.JapaneseTokenizer
import io.bluetape4k.tokenizer.japanese.utils.JapaneseDictionaryProvider
import io.bluetape4k.tokenizer.model.BlockwordRequest
import io.bluetape4k.tokenizer.model.BlockwordResponse

/**
 * 일본어 형태소 분석을 기반으로 다양한 기능을 제공합니다.
 */
object JapaneseProcessor {

    /**
     * 일본어 형태소 분석을 수행합니다.
     *
     * ```
     * val tokens: List<Token> = JapaneseTokenizer.tokenize("お寿司が食べたい。")
     *
     * // 결과
     * token=お: 接頭詞,名詞接続,*,*,*,*,お,オ,オ, 0
     * token=寿司: 名詞,一般,*,*,*,*,寿司,スシ,スシ, 1
     * token=が: 助詞,格助詞,一般,*,*,*,が,ガ,ガ, 3
     * token=食べ: 動詞,自立,*,*,一段,連用形,食べる,タベ,タベ, 4
     * token=たい: 助動詞,*,*,*,特殊・タイ,基本形,たい,タイ,タイ, 6
     * token=。: 記号,句点,*,*,*,*,。,。,。, 8
     * ```
     */
    fun tokenize(text: String): List<Token> {
        return JapaneseTokenizer.tokenize(text)
    }

    fun filter(tokens: List<Token>, predicate: (Token) -> Boolean): List<Token> {
        return JapaneseTokenizer.filter(tokens, predicate)
    }

    fun filterNoun(tokens: List<Token>): List<Token> {
        return JapaneseTokenizer.filterNoun(tokens)
    }

    /**
     * 금칙어에 해당하는 단어를 찾습니다.
     *
     * @param text 일본어 문장
     * @return 금칙어에 해당하는 [Token] 리스트
     */
    fun findBlockwords(text: String): List<Token> {
        return JapaneseBlockwordProcessor.findBlockwords(text)
    }

    /**
     * 일본어 문장에서 금칙어를 마스킹 합니다.
     *
     * @param request 금칙어 처리 요청 정보 [BlockwordRequest]
     * @return 금칙어를 처리한 결과 [BlockwordResponse]
     */
    fun maskBlockwords(request: BlockwordRequest): BlockwordResponse {
        return JapaneseBlockwordProcessor.maskBlockwords(request)
    }

    /**
     * 금칙어를 사전에 추가합니다.
     *
     * @param words 추가할 금칙어
     */
    fun addBlockwords(words: List<String>) {
        JapaneseDictionaryProvider.addBlockwords(words)
    }

    /**
     * 사전에서 해당 금칙어를 삭제합니다.
     *
     * @param words 삭제할 금칙어
     */
    fun removeBlockwords(words: List<String>) {
        JapaneseDictionaryProvider.removeBlockwords(words)
    }

    /**
     * 모든 금칙어를 삭제합니다.
     */
    fun clearBlockwords() {
        JapaneseDictionaryProvider.clearBlockwords()
    }
}
