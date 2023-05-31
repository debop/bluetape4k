package io.bluetape4k.utils.images.compressor

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.coerce
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter

/**
 * JPG 이미지를 압축합니다.
 *
 * @property compressionQuality 압축 품질 (0.3 ~ 0.9 범위의 값을 지정하세요)
 */
class JpgCompressor private constructor(val compressionQuality: Float): ImageCompressor {

    companion object: KLogging() {
        /**
         * Default Compression Quality (0.5F)
         */
        const val DEFAULT_COMPRESSION_QUALITY = 0.5F

        @JvmStatic
        operator fun invoke(compressionQuality: Float = DEFAULT_COMPRESSION_QUALITY): JpgCompressor {
            val cq = compressionQuality.coerce(0.2F, 0.9F)
            return JpgCompressor(cq)
        }
    }

    override fun compress(input: InputStream): ByteArray {
        return compress(input, compressionQuality)
    }

    internal fun compress(input: InputStream, compressionQuality: Float): ByteArray {
        val image = ImageIO.read(input)

        return ByteArrayOutputStream().use { bos ->
            ImageIO.createImageOutputStream(bos).use { ios ->
                val writer: ImageWriter = ImageIO.getImageWritersByFormatName("jpg").next()
                try {
                    writer.output = ios
                    val param = writer.defaultWriteParam
                    param.compressionMode = ImageWriteParam.MODE_EXPLICIT
                    param.compressionQuality = compressionQuality

                    writer.write(null, IIOImage(image, null, null), param)
                } finally {
                    writer.dispose()
                }
                bos.toByteArray()
            }
        }
    }
}
