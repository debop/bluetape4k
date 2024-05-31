package io.wrtn.kommons.captcha

import io.wrtn.kommons.captcha.config.CaptchaConfig

interface CaptchaGenerator<T> {

    var config: CaptchaConfig

    val captchaCodeGenerator: CaptchaCodeGenerator

    fun generate(): Captcha<T>

}
