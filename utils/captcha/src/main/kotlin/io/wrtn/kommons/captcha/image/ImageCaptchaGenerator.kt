package io.wrtn.kommons.captcha.image

import com.sksamuel.scrimage.ImmutableImage
import io.wrtn.kommons.captcha.CaptchaCodeGenerator
import io.wrtn.kommons.captcha.CaptchaGenerator
import io.wrtn.kommons.captcha.config.CaptchaConfig
import io.wrtn.kommons.captcha.utils.FontProvider
import io.wrtn.kommons.images.useGraphics
import io.wrtn.kommons.logging.KLogging
import io.wrtn.kommons.logging.debug
import io.wrtn.kommons.logging.warn
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import kotlin.random.Random

class ImageCaptchaGenerator(
    override var config: CaptchaConfig = CaptchaConfig(),
    override val captchaCodeGenerator: CaptchaCodeGenerator = DefaultCaptchaCodeGenerator,
): CaptchaGenerator<ImmutableImage> {

    companion object: KLogging() {
        @JvmStatic
        val DefaultCaptchaCodeGenerator = CaptchaCodeGenerator(symbols = CaptchaCodeGenerator.UPPER)
    }

    private val fonts: Array<Font> by lazy {
        if (config.fontPaths.isNotEmpty()) {
            loadCustomFonts().toTypedArray()
        } else {
            FontProvider.loadAllFontsFromResource(CaptchaConfig.DEFAULT_FONTS_IN_RESOURCE).toTypedArray()
        }
    }

    override fun generate(): ImageCaptcha {
        // Captcha Code 생성
        val code = captchaCodeGenerator.next(config.length.coerceAtLeast(4))
        val captchaImage = ImmutableImage.create(config.width, config.height, BufferedImage.TYPE_INT_RGB)

        captchaImage.useGraphics { graphics ->
            drawCode(graphics, code)
            if (config.noiseCount > 0) {
                drawNoise(graphics)
            }
        }

        return ImageCaptcha(code, captchaImage)
    }

    /**
     * 해당 Captcha Code를 이미지에 그립니다.
     *
     * @param graphics
     * @param code
     */
    private fun drawCode(graphics: Graphics2D, code: String) {
        graphics.color = config.backgroundColor
        graphics.fillRect(0, 0, config.width, config.height)
        graphics.font = getRandomFont()

        log.debug { "Draw code. code=$code, width=${config.width}, height=${config.height}" }

        val fontMetrics = graphics.fontMetrics
        val totalWidth = fontMetrics.stringWidth(code)    // 지정한 문자열을 모두 그리는데 필요한 폭
        val charGap = (config.width - totalWidth) / (code.length + 1) - 2
        var x = charGap
        val y = (config.height - fontMetrics.height) / 2 + fontMetrics.ascent

        code.forEach { ch ->
            graphics.font = getRandomFont()
            graphics.color = getRandomColor()

            val charWidth = graphics.fontMetrics.charWidth(ch.code)
            val charX = x + charWidth / 2
            val angle = Random.nextInt(-20, 20)

            log.debug { "draw string at ($charX, $y), angle=$angle, charGap=$charGap, x=$x, y=$y" }
            graphics.rotate(Math.toRadians(angle.toDouble()), charX.toDouble(), y.toDouble())
            graphics.drawString(ch.toString(), charX, y)

            // rotate는 reset 을 해줘야 합니다. (그래야 angle 이 누적이 아니라 매번 새로운 각도로 설정됩니다.)
            graphics.rotate(-Math.toRadians(angle.toDouble()), charX.toDouble(), y.toDouble())
            x += charWidth + charGap
        }
    }

    /**
     * Draw captcha noise
     *
     * @param graphics
     */
    private fun drawNoise(graphics: Graphics) {
        graphics.color = getRandomColor()

        for (i in 0 until config.noiseCount) {
            val x1 = Random.nextInt(config.width)
            val y1 = Random.nextInt(config.height)
            val x2 = Random.nextInt(config.width)
            val y2 = Random.nextInt(config.height)

            graphics.color = getRandomColor()
            graphics.drawLine(x1, y1, x2, y2)
        }
    }

    /**
     * [CaptchaConfig.fontPaths]에 지정된 폰트 파일을 로드합니다.
     */
    private fun loadCustomFonts(): List<Font> {
        return config.fontPaths.mapNotNull { path ->
            try {
                log.debug { "Loading font file. path=$path" }
                val fontFile = File(path)
                if (fontFile.exists()) {
                    Font.createFont(Font.TRUETYPE_FONT, fontFile)
                } else null
            } catch (e: Exception) {
                log.warn(e) { "Failed to load font file. path=$path" }
                null
            }
        }
    }

    private fun getRandomFont(): Font {
        val style = config.fontStyles.random()
        val size = config.height.toFloat() / 2 - Random.nextInt(10, 15)
        return fonts.random().deriveFont(style, size)
    }

    private fun getRandomColor(): Color {
        return config.themePalette.random()
    }
}
