package io.bluetape4k.utils.images.splitter

import io.bluetape4k.core.requirePositiveNumber
import io.bluetape4k.coroutines.flow.async
import io.bluetape4k.io.toByteArray
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.utils.images.ImageFormat
import io.bluetape4k.utils.images.compressor.ImageCompressor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.imageio.ImageIO

class ImageSplitter private constructor(val defaultMaxHeight: Int) {

    companion object: KLogging() {
        const val DEFAULT_MIN_HEIGHT = 128
        const val DEFAULT_MAX_HEIGHT = 2048

        @JvmStatic
        operator fun invoke(maxHeight: Int = DEFAULT_MAX_HEIGHT): ImageSplitter {
            return ImageSplitter(maxHeight.coerceAtLeast(DEFAULT_MIN_HEIGHT))
        }
    }

    fun split(
        input: InputStream,
        format: ImageFormat = ImageFormat.JPG,
        splitHeight: Int = this.defaultMaxHeight,
    ): Flow<ByteArray> {
        splitHeight.requirePositiveNumber("splitHeight")
        val height = splitHeight.coerceAtLeast(DEFAULT_MIN_HEIGHT)
        log.debug { "Split image. format=$format, split height=$height" }

        val source = ImageIO.read(input)
        val srcHeight = source.height
        val srcWidth = source.width

        if (srcHeight <= height) {
            return flowOf(input.toByteArray())
        }

        return flow {
            getHeights(height, srcHeight)
                .async { h ->
                    ByteArrayOutputStream().use { bos ->
                        val splitImage = source.getSubimage(0, h, srcWidth, height.coerceAtMost(srcHeight - h))
                        ImageIO.write(splitImage, format.name, bos)
                        bos.toByteArray()
                    }
                }
                .collect {
                    emit(it)
                }
        }
    }

    fun splitAndCompress(
        input: InputStream,
        format: ImageFormat = ImageFormat.JPG,
        splitHeight: Int = this.defaultMaxHeight,
        compressor: ImageCompressor,
    ): Flow<ByteArray> {
        return flow {
            split(input, format, splitHeight)
                .buffer()
                .async {
                    ByteArrayInputStream(it).use { bis ->
                        compressor.compress(bis)
                    }
                }
                .collect {
                    emit(it)
                }
        }
    }

    private fun getHeightWithIndex(height: Int, sourceHeight: Int): Flow<Pair<Int, Int>> = flow {
        var index = 0
        var y = 0
        while (y < sourceHeight) {
            emit(index to y)
            index++
            y += height
        }
    }

    private fun getHeights(height: Int, maxHeight: Int): Flow<Int> = flow {
        var y = 0
        while (y < maxHeight) {
            emit(y)
            y += height
        }
    }
}
