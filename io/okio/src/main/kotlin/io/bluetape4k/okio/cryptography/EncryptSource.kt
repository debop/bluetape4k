package io.bluetape4k.okio.cryptography

import io.bluetape4k.cryptography.encrypt.Encryptor
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.requireGe
import okio.Buffer
import okio.ForwardingSource
import okio.Source

open class EncryptSource(
    delegate: Source,
    val encryptor: Encryptor,
): ForwardingSource(delegate) {

    companion object: KLogging()

    private val sourceBuffer = Buffer()
    private val decryptedBuffer = Buffer()

    override fun read(sink: Buffer, byteCount: Long): Long {
        // 요청한 바이트 수(또는 가능한 모든 바이트) 반환
        val bytesToRead = byteCount.coerceAtMost(sink.size)
        bytesToRead.requireGe(0, "bytesToRead")

        var streamEnd = false
        while (sourceBuffer.size < bytesToRead && !streamEnd) {
            val bytesRead = super.read(sourceBuffer, bytesToRead - sourceBuffer.size)
            if (bytesRead < 0) {
                streamEnd = true
            }
        }

        // source로 부터 읽은 데이터를 복호화
        val bytes = sourceBuffer.readByteArray()
        if (bytes.isNotEmpty()) {
            val decrypted = encryptor.decrypt(bytes)
            decryptedBuffer.write(decrypted)
        }

        // 요청한 바이트 수(또는 가능한 모든 바이트) 만큼 sink에 쓰기
        sink.write(decryptedBuffer, byteCount.coerceAtMost(decryptedBuffer.size))

        return if (decryptedBuffer.size > 0) decryptedBuffer.size else -1
    }
}
