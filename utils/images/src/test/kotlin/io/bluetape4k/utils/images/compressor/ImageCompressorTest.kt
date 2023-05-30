package io.bluetape4k.utils.images.compressor

import io.bluetape4k.io.utils.Resourcex
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.utils.images.AbstractImageTest
import io.bluetape4k.utils.images.ImageFormat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

class ImageCompressorTest: AbstractImageTest() {

    companion object: KLogging()

    /**
     * Compress jpg images
     *
     * cafe original=3,061,079, compressed=1,066,136
     * landscape original=3,525,452, compressed=1,193,717
     */
    @ParameterizedTest(name = "compress jpg file {0}.jpg")
    @ValueSource(strings = ["cafe", "landscape"])
    fun `compress jpg images`(filename: String) {
        val origin = Resourcex.getInputStream("images/$filename.jpg")!!
        val originalSize = origin.available()
        val compressed = ImageCompressors.compress(origin, ImageFormat.JPG)
        val compressedSize = compressed.size

        log.debug { "$filename original=$originalSize, compressed=$compressedSize" }

        val path = Path.of("src/test/resources/images/compressed/${filename}.jpg")
        Files.write(path, compressed, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    }

    @ParameterizedTest(name = "compress png file {0}.png")
    @ValueSource(strings = ["cafe", "landscape"])
    fun `compress png images`(filename: String) {
        val origin = Resourcex.getInputStream("images/$filename.png")!!
        val originalSize = origin.available()
        val compressed = ImageCompressors.compress(origin, ImageFormat.PNG)
        val compressedSize = compressed.size

        log.debug { "$filename original=$originalSize, compressed=$compressedSize" }

        val path = Path.of("src/test/resources/images/compressed/${filename}.png")
        Files.write(path, compressed, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    }
}
