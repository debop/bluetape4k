package io.bluetape4k.utils.images

import io.bluetape4k.junit5.folder.TempFolder
import io.bluetape4k.junit5.folder.TempFolderTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import java.awt.Image
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

@TempFolderTest
class ImageFileExamples: AbstractImageTest() {

    companion object: KLogging()

    @Test
    fun `load large image file`() {
        getImage("images/aqua.jpg").use { input ->
            val image: BufferedImage = ImageIO.read(input)
            image.height shouldBeGreaterThan 0
            image.width shouldBeGreaterThan 0

            log.debug { "Image height=${image.height}, width=${image.width}" }
            log.debug { "Image tile height=${image.tileHeight}, width=${image.tileWidth}" }

            image.propertyNames?.forEach {
                log.debug { "property name=$it, value=${image.getProperty(it)}" }
            }
            log.debug { "image=$image" }
        }
    }

    @Test
    fun `create thumbnail image`(tempFolder: TempFolder) {
        getImage("images/cafe.jpg").use { input ->
            val scaled = BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)

            // HINT: 이 방식은 느리다. BufferedImage.scale 함수를 사용하세요.
            val scaledImage = ImageIO.read(input).getScaledInstance(100, 100, Image.SCALE_SMOOTH)

            scaled.drawImage(scaledImage, 0, 0)

            val file = tempFolder.createFile()
            ImageIO.write(scaled, "jpg", file).shouldBeTrue()
        }
    }
}
