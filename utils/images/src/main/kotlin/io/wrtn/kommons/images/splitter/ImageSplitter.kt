package io.wrtn.kommons.images.splitter

import io.wrtn.kommons.coroutines.flow.async
import io.wrtn.kommons.images.ImageFormat
import io.wrtn.kommons.images.coroutines.CoImageWriter
import io.wrtn.kommons.images.coroutines.CoJpegWriter
import io.wrtn.kommons.images.immutableImageOf
import io.wrtn.kommons.io.toByteArray
import io.wrtn.kommons.logging.KLogging
import io.wrtn.kommons.logging.debug
import io.wrtn.kommons.support.requirePositiveNumber
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.imageio.ImageIO

/**
 * 아주 큰 이미지를 [defaultMaxHeight] 크기로 분할하는 ImageSplitter
 *
 * @property defaultMaxHeight 분할할 이미지의 Height
 */
class ImageSplitter private constructor(val defaultMaxHeight: Int) {

    companion object: KLogging() {
        const val DEFAULT_MIN_HEIGHT = 128
        const val DEFAULT_MAX_HEIGHT = 2048

        @JvmStatic
        operator fun invoke(maxHeight: Int = DEFAULT_MAX_HEIGHT): ImageSplitter {
            return ImageSplitter(maxHeight.coerceAtLeast(DEFAULT_MIN_HEIGHT))
        }
    }

    /**
     * [input] 이미지를 [format] 으로 변환하고 [splitHeight] 만큼 분할하여 [ByteArray] 로 반환합니다.
     *
     * @param input         원본 이미지 정보
     * @param format        변환할 이미지 포맷 (JPG, PNG ...)
     * @param splitHeight   분할할 이미지의 Height
     * @return 분할된 이미지 정보의 Flow
     */
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

        return channelFlow {
            getHeights(height, srcHeight)
                .async { h ->
                    ByteArrayOutputStream().use { bos ->
                        val splitImage = source.getSubimage(0, h, srcWidth, height.coerceAtMost(srcHeight - h))
                        ImageIO.write(splitImage, format.name, bos)
                        bos.toByteArray()
                    }
                }
                .collect {
                    send(it)
                }
        }
    }

    /**
     * [input] 이미지를 [format] 으로 변환하고 [splitHeight] 만큼 분할하여 [ByteArray] 로 반환합니다.
     *
     * @param input         원본 이미지 정보
     * @param format        변환할 이미지 포맷 (JPG, PNG ...)
     * @param splitHeight   분할할 이미지의 Height
     * @param writer        이미지를 변환할 Writer (기본: [CoJpegWriter])
     * @return 분할된 이미지 정보의 Flow
     */
    fun splitAndCompress(
        input: InputStream,
        format: ImageFormat = ImageFormat.JPG,
        splitHeight: Int = this.defaultMaxHeight,
        writer: CoImageWriter = CoJpegWriter.Default,
    ): Flow<ByteArray> {
        return channelFlow {
            split(input, format, splitHeight).buffer()
                .async { bytes ->
                    immutableImageOf(bytes).forWriter(writer).bytes()
                }
                .collect {
                    send(it)
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
