package io.bluetape4k.tokenizer.model

import io.bluetape4k.support.requireNotBlank

data class TokenizeRequest(
    val text: String,
    val options: TokenizeOptions = TokenizeOptions.DEFAULT,
): AbstractMessage() {
    init {
        text.requireNotBlank("text")
    }
}

fun tokenizeRequestOf(
    text: String,
    options: TokenizeOptions = TokenizeOptions.DEFAULT,
): TokenizeRequest {
    text.requireNotBlank("text")
    return TokenizeRequest(text, options)
}
