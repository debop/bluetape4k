package io.bluetape4k.tokenizer.japanese.tokenizer

import com.atilika.kuromoji.ipadic.Token
import com.atilika.kuromoji.ipadic.Tokenizer
import io.bluetape4k.logging.KLogging

/**
 * Kuromoji Japanese tokenizer
 */
object JapaneseTokenizer: KLogging() {

    internal val tokenizer: Tokenizer by lazy { Tokenizer.Builder().build() }

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
        return tokenizer.tokenize(text)
    }

    fun filter(tokens: List<Token>, predicate: (Token) -> Boolean): List<Token> {
        return tokens.filter(predicate)
    }

    fun filterNoun(tokens: List<Token>): List<Token> {
        return filter(tokens) { it.isNoun() }
    }
}
