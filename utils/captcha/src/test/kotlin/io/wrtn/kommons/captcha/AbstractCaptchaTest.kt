package io.wrtn.kommons.captcha

import io.wrtn.kommons.junit5.faker.Fakers
import io.wrtn.kommons.logging.KLogging

abstract class AbstractCaptchaTest {

    companion object: KLogging() {
        @JvmStatic
        val faker = Fakers.faker
    }
}
