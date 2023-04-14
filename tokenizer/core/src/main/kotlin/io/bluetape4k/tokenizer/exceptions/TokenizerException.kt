package io.bluetape4k.tokenizer.exceptions

/**
 * Tokenizer 모듈의 기본 예외
 */
open class TokenizerException: RuntimeException {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable?): super(cause)
}
