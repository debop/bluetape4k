package io.bluetape4k.captcha

import io.bluetape4k.captcha.config.CaptchaConfig

interface CaptchaGenerator<T> {

    var config: CaptchaConfig

    val captchaCodeGenerator: CaptchaCodeGenerator

    fun generate(): Captcha<T>

}
