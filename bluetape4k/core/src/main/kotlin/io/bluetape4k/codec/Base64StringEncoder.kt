package io.bluetape4k.codec

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.EMPTY_STRING
import io.bluetape4k.support.emptyByteArray
import io.bluetape4k.support.toUtf8Bytes
import java.util.*

/**
 * 문자열을 Base64 형태로 인코딩/디코딩 합니다
 */
class Base64StringEncoder: StringEncoder {

    companion object: KLogging() {
        private val encoder by lazy { Base64.getUrlEncoder() }
        private val decoder by lazy { Base64.getUrlDecoder() }
    }

    /**
     * [bytes]를 Base64 문자열로 인코딩합니다.
     *
     * @param bytes 인코딩할 바이트 배열
     * @return Base64로 인코딩된 문자열
     */
    override fun encode(bytes: ByteArray?): String {
        return bytes?.run { encoder.encodeToString(this) } ?: EMPTY_STRING
    }

    /**
     * [encoded]를 디코딩하여 [ByteArray]로 만든다
     *
     * @param encoded 디코딩할 Base64 인코딩된 문자열
     * @return 디코딩된 바이트 배열
     */
    override fun decode(encoded: String?): ByteArray {
        return encoded?.takeIf { it.isNotBlank() }?.run { decoder.decode(this.toUtf8Bytes()) }
            ?: emptyByteArray
    }
}
