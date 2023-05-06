package io.bluetape4k.io.netty.buffer

import io.netty.buffer.ByteBuf
import io.netty.util.ReferenceCounted


/**
 * Wrap this [ByteBuf] into a [BitBuf]
 *
 * @return
 */
fun ByteBuf.toBitBuf(): BitBuf = BitBufImpl(this)

/**
 * Wrapper around [ByteBuf] that allows for writing non byte-aligned bits. This implementation maintains the
 * [ByteBuf.readerIndex] and [ByteBuf.writerIndex] of the underlying [ByteBuf]. When writing to the underlying [ByteBuf]
 * however, the [readerIndex] and [writerIndex] will be automatically aligned to the next byte.
 *
 * To create a [BitBuf] use [ByteBuf.toBitBuf] factory method.
 */
interface BitBuf: ReferenceCounted {

    val byteBuf: ByteBuf

    /** The [ByteBuf.capacity] in terms of bits. */
    val capacity: Long

    /** The maximum [ByteBuf.capacity] in terms of bits. */
    val maxCapacity: Long

    /**
     * Reader index in terms of bits. Modifying this reader index also modifies the underlying [ByteBuf.readerIndex].
     */
    var readerIndex: Long

    /** Returns the number of readable bits. */
    fun readableBits(): Long = writerIndex - readerIndex

    /**
     * Writer index in terms of bits. Modifying this writer index also modifies the underlying [ByteBuf.writerIndex].
     */
    var writerIndex: Long

    /** Returns the number of writable bits. */
    fun writableBits(): Long = capacity - writerIndex

    /**
     * Gets a [Boolean] at the specified absolute bit [index] in this buffer. This method does not modify [readerIndex]
     * or [writerIndex] of this buffer or the underlying [ByteBuf]. Unlike [ByteBuf.getBoolean], this method only reads
     * from a single bit, instead of a single [Byte].
     *
     * @throws IndexOutOfBoundsException if the [index] is less than 0 or index + 1 is greater than [capacity]
     */
    fun getBoolean(index: Long): Boolean

    /**
     * Gets a [UInt] at the specified absolute bit [index] in this buffer. This method does not modify [readerIndex]
     * or [writerIndex] of this buffer or the underlying [ByteBuf].
     *
     * @throws IllegalArgumentException if the [amount] is less than 0 or greater than [Int.SIZE_BITS]
     * @throws IndexOutOfBoundsException if the [index] is less than 0 or index + 1 is greater than [capacity]
     */
    fun getUnsignedBits(index: Long, amount: Int): UInt

    /**
     * Sets the [value] at the specified absolute bit [index] in this buffer. This method does not modify [readerIndex]
     * or [writerIndex] of this buffer or the underlying [ByteBuf]. Unlike [ByteBuf.setBoolean], this method only sets a
     * single bit, instead of a single byte.
     *
     * @throws IndexOutOfBoundsException if the specified [index] is less than 0 or index + 1 is greater than [capacity]
     */
    fun setBoolean(index: Long, value: Boolean): BitBuf = setBits(index, 1, if (value) 1 else 0)

    /**
     * Sets the [value] at the specified absolute bit [index] in this buffer encoded in [amount] of bits. This method
     * does not modify [readerIndex] or [writerIndex] of this buffer or the underlying [ByteBuf].
     *
     * @throws IllegalArgumentException if the [amount] is less than 0 or greater than [Int.SIZE_BITS]
     * @throws IndexOutOfBoundsException if the specified [index] is less than 0 or index + 1 is greater than [capacity]
     */
    fun setBits(index: Long, amount: Int, value: Int): BitBuf

    /**
     * Gets a [Boolean] at the current [readerIndex] and increases the [readerIndex] by 1 in this buffer. This method
     * also increases the [ByteBuf.readerIndex] in the underlying [ByteBuf], if required.
     *
     * @throws IndexOutOfBoundsException if [readableBits] is less than 1
     */
    fun readBoolean(): Boolean

    /**
     * Gets [amount] of bits as an unsigned value at the current [readerIndex] and increases the [readerIndex] by
     * [amount] in this buffer. This method also increases the [ByteBuf.readerIndex] in the underlying [ByteBuf], if
     * required.
     *
     * @throws IndexOutOfBoundsException if [readableBits] is less than [amount]
     */
    fun readUnsignedBits(amount: Int): UInt

    /**
     * Writes a [Boolean] at the current [writerIndex] and increases the [writerIndex] by 1 in this buffer. This method
     * also increases the [ByteBuf.writerIndex] in the underlying [ByteBuf], if required.
     *
     * @throws IndexOutOfBoundsException if [writableBits] is less than 1
     */
    fun writeBoolean(value: Boolean): BitBuf

    /**
     * Writes a [value] in [amount] of bits at the current [writerIndex] and increases the [writerIndex] by [amount] in
     * this buffer. This method also increases the [ByteBuf.writerIndex] in the underlying [ByteBuf], if required.
     *
     * @throws IndexOutOfBoundsException if [writableBits] is less than [amount]
     */
    fun writeBits(value: Int, amount: Int): BitBuf
}
