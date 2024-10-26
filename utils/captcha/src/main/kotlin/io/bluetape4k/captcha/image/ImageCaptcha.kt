package io.bluetape4k.captcha.image

import com.sksamuel.scrimage.ImmutableImage
import io.bluetape4k.captcha.Captcha

/**
 * 이미지 형태의 Captcha
 *
 * @property image captcha 이미지
 * @property code  captcha 코드
 */
data class ImageCaptcha(
    override val code: String,
    override val content: ImmutableImage,
): Captcha<ImmutableImage>
