package io.wrtn.kommons.captcha.image

import com.sksamuel.scrimage.nio.ImageWriter
import com.sksamuel.scrimage.nio.JpegWriter
import java.nio.file.Path

internal val DEFAULT_IMAGE_WRITER = JpegWriter()

/**
 * [ImageCaptcha]의 이미지를 원하는 포맷의 ByteArray 로 변환합니다.
 *
 * @param writer [ImageWriter] 인스턴스 (기본값: JpegWriter)
 * @return
 */
fun ImageCaptcha.toBytes(writer: ImageWriter = DEFAULT_IMAGE_WRITER): ByteArray {
    return content.bytes(writer)
}

/**
 * [ImageCaptcha]의 이미지를 지정한 [path]에 원하는 포맷의 이미지 파일로 저장합니다.
 *
 * @param path 이미지를 저장할 경로
 * @param writer [ImageWriter] 인스턴스 (기본값: JpegWriter)
 * @return
 */
fun ImageCaptcha.writeToFile(path: Path, writer: ImageWriter = DEFAULT_IMAGE_WRITER): Path {
    return content.forWriter(writer).write(path)
}
