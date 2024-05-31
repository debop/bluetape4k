package io.bluetape4k.io

/**
 * Bit output stream
 *
 * @constructor Create empty Bit output stream
 */
class BitOutputStream(val capacity: Int) {

    private val bytes: ByteArray = ByteArray(capacity / 8)
    private var offset: Int = 0

    private fun currentBit(): Int = offset % 8
    private fun currentLength(): Int = offset / 8

    fun bitsCountUpToByte(): Int = when (val currBit = currentBit()) {
        0    -> 0
        else -> 8 - currBit
    }

    fun toArray(): ByteArray = when (val currLen = currentLength()) {
        bytes.size -> bytes
        else       -> bytes.copyOf(currLen)
    }

    fun writeBits(bitsCount: Int, bits: Int) {
        val bitNum = currentBit()
        val byteNum = currentLength()

        val firstWrite = minOf(8 - bitNum, bitsCount)
        val secondWrite = bitsCount - firstWrite

        bytes[byteNum] = (bytes[byteNum].toInt() or (bits and (1 shl firstWrite) - 1 shl bitNum)).toByte()
        if (secondWrite > 0) {
            bytes[byteNum + 1] =
                (bytes[byteNum + 1].toInt() or (bits.ushr(firstWrite) and (1 shl secondWrite) - 1)).toByte()
        }
        offset += bitsCount
    }
}
