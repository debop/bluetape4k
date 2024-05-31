package io.bluetape4k.idgenerators.ksuid

import io.bluetape4k.codec.BytesBase62
import io.bluetape4k.codec.encodeHexString
import io.bluetape4k.logging.KLogging
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

/**
 * K-Sortable Unique Identifier
 *
 * [ksuid](https://github.com/ksuid/ksuid) 를 참고하여 Kotlin으로 제작
 */
object Ksuid: KLogging() {

    private const val EPOCH = 1_400_000_000L
    private const val TIMESTAMP_LEN = 4
    private const val PAYLOAD_LEN = 16
    private const val MAX_ENCODED_LEN = 27

    private val base62: BytesBase62 = BytesBase62
    private val random: SecureRandom = SecureRandom()

    fun generate(): String {
        return generate(generateTimestamp())
    }

    fun generate(date: Date): String {
        return generate(generateTimestamp(date))
    }

    fun generate(dt: LocalDateTime): String {
        return generate(generateTimestamp(dt))
    }

    private fun generate(timestamp: ByteArray): String {
        val payload = generatePayload()

        val uid = ByteArrayOutputStream().use { bos ->
            runCatching {
                bos.write(timestamp)
                bos.write(payload)
            }
            BytesBase62.encode(bos.toByteArray())
        }
        return if (uid.length > MAX_ENCODED_LEN) uid.substring(0, MAX_ENCODED_LEN) else uid
    }

    fun prettyString(ksuid: String): String {
        val bytes = BytesBase62.decode(ksuid)
        val timestamp = extractTimestamp(bytes)
        val utcTimeString = Instant.ofEpochSecond(timestamp).atZone(ZoneOffset.UTC)

        return """
            |Time = $utcTimeString
            |Timestamp = ${timestamp * 1000}
            |Payload = ${extractPayload(bytes)}
            """.trimMargin()
    }

    private fun generateTimestamp(): ByteArray {
        val epochSecond = Instant.now().epochSecond
        return generateTimestamp(epochSecond)
    }

    private fun generateTimestamp(date: Date): ByteArray {
        val epochSeconds = date.toInstant().epochSecond
        return generateTimestamp(epochSeconds)
    }

    private fun generateTimestamp(dt: LocalDateTime): ByteArray {
        val epochSeconds = dt.toEpochSecond(ZoneOffset.UTC)
        return generateTimestamp(epochSeconds)
    }

    private fun generateTimestamp(epochSecond: Long): ByteArray {
        val timestamp = (epochSecond - EPOCH).toInt()
        return ByteBuffer.allocate(TIMESTAMP_LEN).putInt(timestamp).array()
    }

    private fun generatePayload(): ByteArray {
        return ByteArray(PAYLOAD_LEN).apply {
            random.nextBytes(this)
        }
    }

    private fun extractTimestamp(decodedKsuid: ByteArray): Long {
        val timestamp = decodedKsuid.copyOf(TIMESTAMP_LEN)
        return ByteBuffer.wrap(timestamp).int.toLong() + EPOCH
    }

    private fun extractPayload(decodedKsuid: ByteArray): String {
        val payload = decodedKsuid.copyOfRange(TIMESTAMP_LEN, decodedKsuid.size - TIMESTAMP_LEN)
        return payload.encodeHexString()

    }
}
