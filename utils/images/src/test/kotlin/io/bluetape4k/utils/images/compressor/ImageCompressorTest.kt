package io.bluetape4k.utils.images.compressor

import io.bluetape4k.io.utils.Resourcex
import io.bluetape4k.io.write
import io.bluetape4k.junit5.folder.TempFolder
import io.bluetape4k.junit5.folder.TempFolderTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.utils.images.AbstractImageTest
import io.bluetape4k.utils.images.ImageFormat
import org.amshove.kluent.shouldBeGreaterThan
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@TempFolderTest
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
    fun `compress jpg images`(filename: String, tempFolder: TempFolder) {
        val origin = Resourcex.getInputStream("images/$filename.jpg")!!
        val originalSize = origin.available()
        val compressed = ImageCompressors.compress(origin, ImageFormat.JPG)
        val compressedSize = compressed.size

        log.debug { "$filename original=$originalSize, compressed=$compressedSize" }
        compressedSize shouldBeGreaterThan 0
        tempFolder.createFile().write(compressed)
    }

    /**
     * NOTE: PNG 파일 자체가 JPG에 비해서 커서, 압축 속도가 상당히 느리다.
     */
    @ParameterizedTest(name = "compress png file {0}.png")
    @ValueSource(strings = ["cafe", "landscape"])
    fun `compress png images`(filename: String, tempFolder: TempFolder) {
        val origin = Resourcex.getInputStream("images/$filename.png")!!
        val originalSize = origin.available()
        val compressed = ImageCompressors.compress(origin, ImageFormat.PNG)
        val compressedSize = compressed.size

        log.debug { "$filename original=$originalSize, compressed=$compressedSize" }
        compressedSize shouldBeGreaterThan 0
        tempFolder.createFile().write(compressed)
    }
}
