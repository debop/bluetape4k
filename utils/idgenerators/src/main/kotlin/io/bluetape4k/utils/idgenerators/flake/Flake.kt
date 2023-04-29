package io.bluetape4k.utils.idgenerators.flake

import io.bluetape4k.codec.Url62
import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.idgenerators.IdGenerator
import io.bluetape4k.utils.idgenerators.getMachineId
import java.math.BigInteger
import java.nio.ByteBuffer
import java.time.Clock
import java.util.UUID
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlinx.atomicfu.atomic
import kotlin.concurrent.withLock

typealias NodeIdentifier = () -> Long

/**
 * [Boundary](https://github.com/boundary/flake) 의  Flake ID 생성 알고리즘의 Kotlin 구현체
 *
 * ```
 * val flake = Flake()
 * val id: ByteArray = flake.nextId()  // 128 bit ID
 * val idString = flake.nextIdString() // 128 bit ID as hex string
 * ```
 *
 */
class Flake private constructor(
    private val nodeId: ByteArray,
    private val clock: Clock,
): IdGenerator<ByteArray> {

    companion object: KLogging() {
        private const val MAX_SEQ = 0xFFFF
        private const val ID_SIZE_BYTES = 16
        private const val NODE_ID_BYTES = 6
        private const val HEX_VALUES = "0123456789abcdef"

        operator fun invoke(): Flake {
            return invoke { getMachineId(Int.MAX_VALUE).toLong() }
        }

        operator fun invoke(nodeIdentifier: NodeIdentifier): Flake {
            return invoke(nodeIdentifier, Clock.systemUTC())
        }

        operator fun invoke(nodeIdentifier: NodeIdentifier, clock: Clock): Flake {
            val tmpNodeId = nodeIdentifier()
            val nodeId = ByteArray(NODE_ID_BYTES)
            for (i in 0 until NODE_ID_BYTES) {
                nodeId[i] = ((tmpNodeId shr ((5 - i) * 8)) and 0xFF).toByte()
            }
            return Flake(nodeId, clock)
        }

        fun asHexString(id: ByteArray): String {
            return buildString {
                id.forEach { b ->
                    val first = (b.toInt() and 0xF0) shr 4
                    val second = b.toInt() and 0x0F
                    append(HEX_VALUES[first]).append(HEX_VALUES[second])
                }
            }
        }

        fun asComponentString(id: ByteArray): String {
            val buffer = ByteBuffer.wrap(id)
            val node = ByteArray(NODE_ID_BYTES)
            buffer.get(node)
            return "${buffer.long}-${BigInteger(node).toLong()}-${buffer.short}"
        }

        fun asBase62String(id: ByteArray): String {
            check(id.size == 16) { "id should have size 16 bytes." }
            val buffer = ByteBuffer.wrap(id)
            val msb = buffer.long
            val lsb = buffer.long
            return Url62.encode(UUID(msb, lsb))
        }
    }

    private val lock: Lock = ReentrantLock(true)

    @Volatile
    private var currentTime: Long = clock.millis()

    @Volatile
    private var lastTime: Long = clock.millis()

    private val sequence = atomic(0)

    /**
     * Generate a 128-bit Flake ID
     *
     * @return byte array containing the generated ID
     */
    override fun nextId(): ByteArray {
        lock.withLock {
            updateState()
            val idBuffer = ByteBuffer.allocate(ID_SIZE_BYTES)
            return idBuffer
                .putLong(currentTime)
                .put(nodeId)
                .putShort(sequence.value.toShort())
                .array()
        }
    }

    override fun nextIdAsString(): String {
        return asHexString(nextId())
    }

    private fun updateState() {
        currentTime = clock.millis()

        if (currentTime != lastTime) {
            sequence.value = 0
            lastTime = currentTime
        } else if (sequence.value == MAX_SEQ) {
            while (currentTime <= lastTime) {
                currentTime = clock.millis()
            }
            sequence.value = 0
            lastTime = currentTime
        } else {
            sequence.incrementAndGet()
        }
    }
}
