package io.wrtn.kommons.captcha.utils

import io.wrtn.kommons.captcha.AbstractCaptchaTest
import io.wrtn.kommons.captcha.config.CaptchaConfig
import io.wrtn.kommons.logging.KLogging
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

class FontProviderTest: AbstractCaptchaTest() {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `load resource font`() {
        val path = CaptchaConfig.DEFAULT_FONTS_IN_RESOURCE.random()
        val font = FontProvider.loadFontFromResource(path)
        font.shouldNotBeNull()
    }

    @Test
    fun `load resource fonts`() {
        val fontPaths = CaptchaConfig.DEFAULT_FONTS_IN_RESOURCE
        val fonts = FontProvider.loadAllFontsFromResource(fontPaths)
        fonts.shouldNotBeEmpty()
    }
}
