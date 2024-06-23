package io.bluetape4k.okio.cryptography

import io.bluetape4k.cryptography.encrypt.Encryptor
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.okio.bufferOf
import okio.Buffer
import okio.ForwardingSink
import okio.Sink

open class EncryptSink(
    delegate: Sink,
    val encryptor: Encryptor,
): ForwardingSink(delegate) {

    companion object: KLogging()

    override fun write(source: Buffer, byteCount: Long) {
        // 요청한 바이트 수(또는 가능한 모든 바이트) 반환
        val bytesToRead = byteCount.coerceAtMost(source.size)
        val plainBytes = source.readByteArray(bytesToRead)
        log.debug { "Encrypting: ${plainBytes.size} bytes" }

        // 암호화
        val encrypted = encryptor.encrypt(plainBytes)
        val encryptedSink = bufferOf(encrypted)
        super.write(encryptedSink, encryptedSink.size)
    }
}
