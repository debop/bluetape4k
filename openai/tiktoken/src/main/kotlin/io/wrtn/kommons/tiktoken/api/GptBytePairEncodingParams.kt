package io.bluetape4k.tiktoken.api

import java.util.regex.Pattern

/**
 * Parameter for the byte pair encoding used to tokenize for the OpenAI GPT models.
 *
 * This library supports the encodings that are listed in [EncodingType] out of the box.
 * But if you want to use a custom encoding, you can use this class to pass the parameters to the library.
 * Use [EncodingRegistry#registerGptBytePairEncoding(GptBytePairEncodingParams)] to register your custom encoding
 * to the registry, so that you can easily use your encoding in conjunction with the predefined ones.
 *
 * @property name      the name of the encoding. This is used to identify the encoding and must be unique
 * @property pattern   the pattern that is used to split the input text into tokens.
 * @property encoder   the encoder that maps the tokens to their ids
 * @property specialTokenEncoder the encoder that maps the special tokens to their ids
 */
data class GptBytePairEncodingParams(
    val name: String,
    val pattern: Pattern,
    val encoder: Map<ByteArray, Int>,
    val specialTokenEncoder: Map<String, Int>,
)
