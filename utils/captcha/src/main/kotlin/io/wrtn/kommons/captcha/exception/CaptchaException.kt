package io.wrtn.kommons.captcha.exception

/**
 * Captcha 관련 기본 예외 클래스
 */
open class CaptchaException: RuntimeException {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable?): super(cause)
}
