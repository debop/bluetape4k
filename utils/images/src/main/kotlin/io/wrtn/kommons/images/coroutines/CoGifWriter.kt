package io.wrtn.kommons.images.coroutines

import com.sksamuel.scrimage.AwtImage
import com.sksamuel.scrimage.metadata.ImageMetadata
import com.sksamuel.scrimage.nio.GifWriter
import io.wrtn.kommons.logging.KLogging
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import java.io.OutputStream

/**
 * 비동기 방식으로 gif 이미지를 생성합니다.
 */
class CoGifWriter(
    progressive: Boolean = false,
): GifWriter(progressive), CoImageWriter {

    companion object: KLogging() {
        @JvmStatic
        val Default = CoGifWriter(false)

        @JvmStatic
        val Progressive = CoGifWriter(true)
    }

    override fun withProgressive(progressive: Boolean): CoGifWriter {
        return CoGifWriter(progressive)
    }

    override suspend fun writeSuspending(image: AwtImage, metadata: ImageMetadata, out: OutputStream) {
        withContext(currentCoroutineContext()) {
            write(image, metadata, out)
        }
    }
}
