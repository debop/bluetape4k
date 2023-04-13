package io.bluetape4k.io.compressor

import io.bluetape4k.codec.decodeBase64ByteArray
import io.bluetape4k.codec.encodeBase64String
import io.bluetape4k.core.support.emptyByteArray
import io.bluetape4k.core.support.isNullOrEmpty
import io.bluetape4k.core.support.toUtf8ByteArray
import io.bluetape4k.core.support.toUtf8String
import io.bluetape4k.io.getBytes
import io.bluetape4k.io.toByteArray
import io.bluetape4k.io.toInputStream
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.warn
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * 데이터를 압축/복원합니다.
 */
interface Compressor {

    /**
     * 데이터를 압축합니다.
     *
     * @param plain 원본 데이터
     * @return 압축된 데이터
     */
    fun compress(plain: ByteArray?): ByteArray

    /**
     * 압축된 데이터를 복원합니다.
     *
     * @param compressed 압축된 데이터
     * @return 복원된 데이터
     */
    fun decompress(compressed: ByteArray?): ByteArray

    fun compress(plain: String): String =
        compress(plain.toUtf8ByteArray()).encodeBase64String()

    fun decompress(compressed: String): String =
        decompress(compressed.decodeBase64ByteArray()).toUtf8String()

    fun compress(plainBuffer: ByteBuffer): ByteBuffer =
        ByteBuffer.wrap(compress(plainBuffer.getBytes()))

    fun decompress(compressedBuffer: ByteBuffer): ByteBuffer =
        ByteBuffer.wrap(decompress(compressedBuffer.getBytes()))

    fun compress(plainStream: InputStream): InputStream =
        compress(plainStream.toByteArray()).toInputStream()

    fun decompress(compressedStream: InputStream): InputStream =
        decompress(compressedStream.toByteArray()).toInputStream()
}


/**
 * [Compressor]의 최상위 추상화 클래스입니다.
 */
abstract class AbstractCompressor : Compressor {

    companion object : KLogging()

    protected abstract fun doCompress(plain: ByteArray): ByteArray
    protected abstract fun doDecompress(compressed: ByteArray): ByteArray

    /**
     * 데이터를 압축합니다.
     *
     * @param plain 원본 데이터
     * @return 압축된 데이터
     */
    override fun compress(plain: ByteArray?): ByteArray {
        if (plain.isNullOrEmpty()) {
            return emptyByteArray
        }

        return runCatching { doCompress(plain!!) }
            .onFailure { log.warn(it) { "Fail to compress." } }
            .getOrDefault(emptyByteArray)
    }

    /**
     * 압축된 데이터를 복원합니다.
     *
     * @param compressed 압축된 데이터
     * @return 복원된 데이터
     */
    override fun decompress(compressed: ByteArray?): ByteArray {
        if (compressed.isNullOrEmpty()) {
            return emptyByteArray
        }
        return runCatching { doDecompress(compressed!!) }
            .onFailure { log.warn(it) { "Fail to decompress." } }
            .getOrDefault(emptyByteArray)
    }
}
