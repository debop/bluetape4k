package io.wrtn.kommons.captcha.exception

/**
 * Captcha 에 사용될 Font를 찾지 못할 때 발생하는 예외
 */
class FontLoadException: CaptchaException {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable?): super(cause)
}
