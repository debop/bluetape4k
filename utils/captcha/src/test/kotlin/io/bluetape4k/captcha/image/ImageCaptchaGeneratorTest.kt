package io.bluetape4k.captcha.image

import io.bluetape4k.captcha.AbstractCaptchaTest
import io.bluetape4k.captcha.CaptchaCodeGenerator
import io.bluetape4k.captcha.config.CaptchaConfig
import io.bluetape4k.captcha.config.CaptchaTheme
import io.bluetape4k.logging.KLogging
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.awt.Color
import java.nio.file.Path

class ImageCaptchaGeneratorTest: AbstractCaptchaTest() {

    companion object: KLogging()

    private val config = CaptchaConfig(
        width = 200,
        height = 80,
        length = 6,
        noiseCount = 5,
        theme = CaptchaTheme.DARK,
        darkBackgroundColor = Color.BLACK,
        lightBackgroundColor = Color.WHITE
    )

    private val codeGenerator = mockk<CaptchaCodeGenerator>()

    private lateinit var captchaGenerator: ImageCaptchaGenerator

    @BeforeEach
    fun beforeEach() {
        clearMocks(codeGenerator)
        captchaGenerator = ImageCaptchaGenerator(config, codeGenerator)
    }

    @Test
    fun `generate captcha`() {
        val code = "ABC123"
        every { codeGenerator.next(6) } returns code

        val captcha = captchaGenerator.generate()

        captcha.content.shouldNotBeNull()
        captcha.code shouldBeEqualTo code

        captcha.toBytes().shouldNotBeNull()
        captcha.writeToFile(Path.of("./captcha.jpg"))
    }

    @Test
    fun `random code with UPPER`() {
        config.noiseCount = 6
        val codeGen = CaptchaCodeGenerator(symbols = CaptchaCodeGenerator.UPPER)
        val captchaGen = ImageCaptchaGenerator(config, codeGen)

        repeat(10) {
            config.theme = if (it < 5) CaptchaTheme.DARK else CaptchaTheme.LIGHT
            val captcha = captchaGen.generate()

            captcha.content.shouldNotBeNull()
            captcha.writeToFile(Path.of("./captcha-upper-$it.jpg"))
        }
    }

    @Test
    fun `random code with UPPER and DIGIT`() {
        config.noiseCount = 4
        val codeGen = CaptchaCodeGenerator(symbols = CaptchaCodeGenerator.UPPER_DIGITS)
        val captchaGen = ImageCaptchaGenerator(config, codeGen)

        repeat(10) {
            config.theme = if (it < 5) CaptchaTheme.DARK else CaptchaTheme.LIGHT
            val captcha = captchaGen.generate()

            captcha.content.shouldNotBeNull()

            captcha.writeToFile(Path.of("./captcha-upper-digit-$it.jpg"))
        }
    }
}
