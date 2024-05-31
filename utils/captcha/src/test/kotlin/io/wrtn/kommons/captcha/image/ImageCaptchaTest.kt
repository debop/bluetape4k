package io.wrtn.kommons.captcha.image

import com.sksamuel.scrimage.ImmutableImage
import io.wrtn.kommons.captcha.AbstractCaptchaTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test
import java.awt.image.BufferedImage

class ImageCaptchaTest: AbstractCaptchaTest() {

    @Test
    fun `create ImageCaptcha instance`() {
        val code = "ABC123"
        val image = ImmutableImage.create(100, 50, BufferedImage.TYPE_INT_RGB)

        val captcha = ImageCaptcha(code, image)

        captcha.code shouldBeEqualTo code
        captcha.content shouldBeEqualTo image
    }

    @Test
    fun `captcha image to bytearray`() {
        val code = "ABC123"
        val image = ImmutableImage.create(100, 50, BufferedImage.TYPE_INT_RGB)

        val captcha = ImageCaptcha(code, image)

        val bytes = captcha.toBytes()
        bytes.shouldNotBeEmpty()
    }
}
