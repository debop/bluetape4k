package io.bluetape4k.tokenizer.exceptions

/**
 * 잘못된 요청을 받았을 때 발생하는 예외
 */
class InvalidRequestException: TokenizerException {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable?): super(cause)
}
