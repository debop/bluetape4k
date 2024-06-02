package io.bluetape4k.tokenizer.model

data class TokenizeResponse(
    val text: String,
    val tokens: List<String> = emptyList(),
): AbstractMessage() {

}

fun tokenizeResponseOf(
    text: String,
    tokens: List<String> = emptyList(),
): TokenizeResponse = TokenizeResponse(text, tokens)
