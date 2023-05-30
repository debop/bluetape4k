package io.bluetape4k.utils.images.compressor

import com.googlecode.pngtastic.core.PngImage
import com.googlecode.pngtastic.core.PngOptimizer
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 *  PNG 파일에 대한 압축을 수행합니다.
 *
 *  참고: [Pngtastic](https://github.com/depsypher/pngtastic)
 */
class PngCompressor: ImageCompressor {

    private val optimizer = PngOptimizer()

    override fun compress(input: InputStream): ByteArray {

        val image = PngImage(input)
        val optimizedImg = optimizer.optimize(image, true, 6)

        return ByteArrayOutputStream().use { bos ->
            optimizedImg.writeDataOutputStream(bos)
            bos.toByteArray()
        }
    }
}
