package io.bluetape4k.utils.images

import io.bluetape4k.junit5.folder.TempFolder
import io.bluetape4k.junit5.folder.TempFolderTest
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterThan
import org.junit.jupiter.api.Test
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

@TempFolderTest
class BufferedImageSupportTest: AbstractImageTest() {

    companion object: KLogging()

    private val needManualView = false

    private fun BufferedImage.writeJpg(path: String) {
        if (needManualView) {
            this.write(ImageFormat.JPG, path)
        }
    }

    @Test
    fun `이미지를 비율로 scale 한다`(tempFolder: TempFolder) {
        getImage("images/cafe.jpg").use { input ->
            val image = ImageIO.read(input)
            val scaled = image.scale(0.15)

            scaled.width shouldBeGreaterThan 0
            scaled.height shouldBeGreaterThan 0

            scaled.writeJpg("cafe_ratio.jpg")
            ImageIO.write(scaled, "jpg", tempFolder.createFile())
        }
    }

    @Test
    fun `이미지를 특정 크기 100x100로 Scaling한다`(tempFolder: TempFolder) {
        getImage("images/cafe.jpg").use { input ->
            val image = ImageIO.read(input)
            val scaled = image.scale(100, 100, proportional = false)

            scaled.width shouldBeEqualTo 100
            scaled.height shouldBeEqualTo 100

            scaled.writeJpg("cafe_fixed.jpg")
            scaled.write(ImageFormat.JPG, tempFolder.createFile())
        }
    }

    @Test
    fun `이미지를 특정 크기 100x100을 비례적으로 Scaling 한다`(tempFolder: TempFolder) {
        getImage("images/cafe.jpg").use { input ->
            val image = ImageIO.read(input)
            val scaled = image.scale(100, 100)

            if (image.width > image.height) {
                scaled.width shouldBeEqualTo 100
            } else {
                scaled.height shouldBeEqualTo 100
            }

            scaled.writeJpg("cafe_proportional.jpg")
            scaled.write(ImageFormat.JPG, tempFolder.createFile())
        }
    }
}
