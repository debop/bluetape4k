package io.bluetape4k.captcha.config

import io.bluetape4k.captcha.AbstractCaptchaTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test

class CaptchaConfigTest: AbstractCaptchaTest() {

    companion object: KLogging()

    @Test
    fun `default captcha config`() {
        val config = CaptchaConfig()

        config.width shouldBeEqualTo CaptchaConfig.DEFAULT_WIDTH
        config.height shouldBeEqualTo CaptchaConfig.DEFAULT_HEIGHT
        config.length shouldBeEqualTo CaptchaConfig.DEFAULT_LENGTH
        config.noiseCount shouldBeEqualTo CaptchaConfig.DEFAULT_NOISE_COUNT

        config.lightPalette shouldBeEqualTo CaptchaConfig.DEFAULT_LIGHT_PALETTE
        config.darkPalette shouldBeEqualTo CaptchaConfig.DEFAULT_DARK_PALETTE

        config.fontStyles shouldBeEqualTo CaptchaConfig.DEFAULT_FONT_STYLES
        config.fontPaths.shouldBeEmpty()

        log.debug { "config=$config" }
    }

    @Test
    fun `config themes`() {
        val config = CaptchaConfig()

        config.theme = CaptchaTheme.LIGHT
        config.isLightTheme.shouldBeTrue()
        config.themePalette shouldBeEqualTo CaptchaConfig.DEFAULT_LIGHT_PALETTE
        config.backgroundColor shouldBeEqualTo CaptchaConfig.DEFAULT_LIGHT_BG_COLOR

        config.theme = CaptchaTheme.DARK
        config.isDarkTheme.shouldBeTrue()
        config.themePalette shouldBeEqualTo CaptchaConfig.DEFAULT_DARK_PALETTE
        config.backgroundColor shouldBeEqualTo CaptchaConfig.DEFAULT_DARK_BG_COLOR
    }
}
