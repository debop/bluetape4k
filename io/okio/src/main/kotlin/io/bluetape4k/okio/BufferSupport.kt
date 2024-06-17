package io.bluetape4k.okio

import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import okio.ByteString
import okio.ByteString.Companion.toByteString
import okio.Sink
import okio.Source

/**
 * [Buffer]를 [BufferedSource]로 변환합니다.
 */
fun Buffer.asBufferedSource(): BufferedSource = (this as Source).buffered()

/**
 * [Buffer]를 [BufferedSink]로 변환합니다.
 */
fun Buffer.asBufferedSink(): BufferedSink = (this as Sink).buffered()

/**
 * [text]를 담은 [Buffer]를 생성합니다.
 *
 * @param text Buffer에 쓸 UTF-8 텍스트
 * @return [Buffer] 인스턴스
 */
fun bufferOf(text: String): Buffer = Buffer().writeUtf8(text)

/**
 * [bytes]를 담은 [Buffer]를 생성합니다.
 *
 * @param bytes Buffer에 쓸 [ByteArray]
 * @return [Buffer] 인스턴스
 */
fun bufferOf(bytes: ByteArray): Buffer = Buffer().write(bytes.toByteString())

/**
 * [byteString]을 담은 [Buffer]를 생성합니다.
 *
 * @param byteString Buffer에 쓸 [ByteString]
 * @return [Buffer] 인스턴스
 */
fun bufferOf(byteString: ByteString): Buffer = Buffer().write(byteString)

/**
 * [source] 내용을 복사한 [Buffer]를 생성합니다.
 */
fun bufferOf(source: Buffer, offset: Long = 0L, size: Long = source.size): Buffer {
    return Buffer().apply {
        source.clone().copyTo(this, offset, size)
    }
}
