package io.bluetape4k.io

import io.bluetape4k.support.requireInRange


/**
 * Bit 단위로 읽기를 수행하는 InputStream
 *
 * @property bytes 읽을 Byte Array
 */
class BitInputStream(private val bytes: ByteArray) {

    private val bitLength: Int = bytes.size * 8
    private var offset: Int = 0

    fun hasMore(): Boolean = offset < bitLength

    fun seekBit(pos: Int) {
        offset += pos
        if (offset < 0 || offset > bitLength) {
            throw IndexOutOfBoundsException("Invalid offset. offset=$offset, pos=$pos")
        }
    }

    fun readBits(bitsCount: Int): Int {
        bitsCount.requireInRange(0, 8, "bitsCount")

        val bitNum = offset % 8
        val byteNum = offset / 8

        val firstRead = minOf(8 - bitNum, bitsCount)
        val secondRead = bitsCount - firstRead

        var result = (bytes[byteNum].toInt() and ((1 shl firstRead) - 1 shl bitNum)).ushr(bitNum)
        if (secondRead > 0 && bytes.size > byteNum + 1) {
            result = result or (bytes[byteNum + 1].toInt() and ((1 shl secondRead) - 1) shl firstRead)
        }
        offset += bitsCount
        return result
    }
}
