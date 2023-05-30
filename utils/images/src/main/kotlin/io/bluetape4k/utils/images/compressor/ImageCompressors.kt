package io.bluetape4k.utils.images.compressor

import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.images.ImageFormat
import java.io.InputStream

object ImageCompressors: KLogging() {

    private val jpgCompressor by lazy { JpgCompressor() }
    private val pngCompressor by lazy { PngCompressor() }

    /**
     * JPG, PNG 이미지를 최적화하여 데이터 사이즈를 축소합니다.
     * 단 사진 같은 경우 JPG의 크기가 PNG 에 비해 1/10 이므로 JPG 를 사용하는 것을 추천합니다.
     *
     * @param input  이미지 파일의 [InputStream]
     * @param format 이미지 포맷 (JPG or PNG)
     * @param compressionQuality 압축 품질 (0 ~ 1 사이 값인데, 0.3 ~ 0.9 를 추천합니다)
     * @return
     */
    fun compress(
        input: InputStream,
        format: ImageFormat = ImageFormat.JPG,
        compressionQuality: Float = JpgCompressor.DEFAULT_COMPRESSION_QUALITY,
    ): ByteArray = when (format) {
        ImageFormat.JPG -> jpgCompressor.compress(input, compressionQuality)
        ImageFormat.PNG -> pngCompressor.compress(input)
    }
}
