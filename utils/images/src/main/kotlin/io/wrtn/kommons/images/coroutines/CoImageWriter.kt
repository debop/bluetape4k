package io.wrtn.kommons.images.coroutines

import com.sksamuel.scrimage.AwtImage
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.metadata.ImageMetadata
import com.sksamuel.scrimage.nio.ImageWriter
import java.io.OutputStream

/**
 * 비동기 방식으로 이미지를 생성하는 [ImageWriter] 입니다.
 */
interface CoImageWriter: ImageWriter {

    /**
     * 비동기 방식으로 이미지를 [out]에 씁니다.
     *
     * @param image   이미지
     * @param metadata
     * @param out    쓰기용 [OutputStream]
     */
    suspend fun writeSuspending(image: AwtImage, metadata: ImageMetadata, out: OutputStream)

    /**
     * 비동기 방식으로 이미지를 [out]에 씁니다.
     *
     * @param image  이미지
     * @param out    쓰기 대상 [OutputStream]
     */
    suspend fun writeSuspending(image: ImmutableImage, out: OutputStream) {
        writeSuspending(image, image.metadata, out)
    }

    /**
     * 이미지를 [out]에 씁니다.
     *
     * @param image   이미지
     * @param out    쓰기용 [OutputStream]
     */
    fun write(image: ImmutableImage, out: OutputStream) {
        write(image, image.metadata, out)
    }
}
