package io.bluetape4k.okio.base64

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.okio.bufferOf
import okio.Buffer
import okio.ForwardingSink
import okio.Sink

/**
 * 데이터를 Base64로 인코딩하여 [Sink]에 쓰는 [Sink] 구현체.
 *
 * @see ForwardingSink
 * @see Sink
 * @see Base64Source
 */
class Base64Sink(delegate: Sink): ForwardingSink(delegate) {

    companion object: KLogging()

    /**
     * [source]로부터 [byteCount]만큼 읽어 Base64로 인코딩한 후 [Sink]에 쓴다.
     */
    override fun write(source: Buffer, byteCount: Long) {
        val bytesToRead = byteCount.coerceAtMost(source.size)
        val readByteString = source.readByteString(bytesToRead)

        // Base64 encode
        val encodedSink = bufferOf(readByteString.base64())
        log.debug { "Write Base64 encoded data: $encodedSink" }
        super.write(encodedSink, encodedSink.size)
    }
}
