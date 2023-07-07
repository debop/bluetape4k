package io.bluetape4k.multibase.binary

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.toUtf8String

/**
 * Provides Base32 encoding and decoding as defined by <a href="http://www.ietf.org/rfc/rfc4648.txt">RFC 4648</a>.
 *
 * From https://commons.apache.org/proper/commons-codec/
 *
 * <p>
 * The class can be parameterized in the following manner with various constructors:
 * </p>
 * <ul>
 * <li>Whether to use the "base32hex" variant instead of the default "base32"</li>
 * <li>Line length: Default 76. Line length that aren't multiples of 8 will still essentially end up being multiples of
 * 8 in the encoded data.
 * <li>Line separator: Default is CRLF ("\r\n")</li>
 * </ul>
 * <p>
 * This class operates directly on byte streams, and not character streams.
 * </p>
 * <p>
 * This class is thread-safe.
 * </p>
 *
 * 참고: [RFC 4648](http://www.ietf.org/rfc/rfc4648.txt)
 */
class Base32(
    lineLength: Int = 0,
    lineSeparator: ByteArray? = null,
    useHex: Boolean = false,
    pad: Byte = PAD_DEFAULT,
): BaseNCodec(BYTES_PER_UNENCODED_BLOCK, BYTES_PER_ENCODED_BLOCK, lineLength, lineSeparator?.size ?: 0, pad) {

    companion object: KLogging() {

        /**
         * BASE32 characters are 5 bits in length.
         * They are formed by taking a block of five octets to form a 40-bit string,
         * which is converted into eight BASE32 characters.
         */
        private const val BITS_PER_ENCODED_BYTE = 5
        private const val BYTES_PER_ENCODED_BLOCK = 8
        private const val BYTES_PER_UNENCODED_BLOCK = 5

        /**
         * Chunk separator per RFC 2045 section 2.1.
         *
         * @see [RFC 2045 section 2.1](http://www.ietf.org/rfc/rfc2045.txt)
         */
        private val CHUNK_SEPARATOR = byteArrayOf('\r'.code.toByte(), '\n'.code.toByte())

        /**
         * This array is a lookup table that translates Unicode characters drawn from the "Base32 Alphabet" (as specified
         * in Table 3 of RFC 4648) into their 5-bit positive integer equivalents. Characters that are not in the Base32
         * alphabet but fall within the bounds of the array are translated to -1.
         */
        private val DECODE_TABLE = byteArrayOf(
            //  0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  // 00-0f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  // 10-1f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  // 20-2f
            -1, -1, 26, 27, 28, 29, 30, 31, -1, -1, -1, -1, -1, -1, -1, -1,  // 30-3f 2-7
            -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,  // 40-4f A-O
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,  // 50-5a P-Z
            -1, -1, -1, -1, -1,  // 5b - 5f
            -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,  // 60 - 6f a-o
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25
        )

        /**
         * This array is a lookup table that translates 5-bit positive integer index values into their "Base32 Alphabet"
         * equivalents as specified in Table 3 of RFC 4648.
         */
        private val ENCODE_TABLE = byteArrayOf(
            'A'.code.toByte(),
            'B'.code.toByte(),
            'C'.code.toByte(),
            'D'.code.toByte(),
            'E'.code.toByte(),
            'F'.code.toByte(),
            'G'.code.toByte(),
            'H'.code.toByte(),
            'I'.code.toByte(),
            'J'.code.toByte(),
            'K'.code.toByte(),
            'L'.code.toByte(),
            'M'.code.toByte(),
            'N'.code.toByte(),
            'O'.code.toByte(),
            'P'.code.toByte(),
            'Q'.code.toByte(),
            'R'.code.toByte(),
            'S'.code.toByte(),
            'T'.code.toByte(),
            'U'.code.toByte(),
            'V'.code.toByte(),
            'W'.code.toByte(),
            'X'.code.toByte(),
            'Y'.code.toByte(),
            'Z'.code.toByte(),
            '2'.code.toByte(),
            '3'.code.toByte(),
            '4'.code.toByte(),
            '5'.code.toByte(),
            '6'.code.toByte(),
            '7'.code.toByte()
        )

        /**
         * This array is a lookup table that translates Unicode characters drawn from the "Base32 Hex Alphabet" (as
         * specified in Table 4 of RFC 4648) into their 5-bit positive integer equivalents. Characters that are not in the
         * Base32 Hex alphabet but fall within the bounds of the array are translated to -1.
         */
        private val HEX_DECODE_TABLE = byteArrayOf(
            //  0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  // 00-0f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  // 10-1f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  // 20-2f
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1,  // 30-3f 2-7
            -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,  // 40-4f A-O
            25, 26, 27, 28, 29, 30, 31,  // 50-56 P-V
            -1, -1, -1, -1, -1, -1, -1, -1, -1,  // 57-5f Z-_
            -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,  // 60-6f `-o
            25, 26, 27, 28, 29, 30, 31 // 70-76 p-v
        )

        /**
         * This array is a lookup table that translates 5-bit positive integer index values into their
         * "Base32 Hex Alphabet" equivalents as specified in Table 4 of RFC 4648.
         */
        private val HEX_ENCODE_TABLE = byteArrayOf(
            '0'.code.toByte(),
            '1'.code.toByte(),
            '2'.code.toByte(),
            '3'.code.toByte(),
            '4'.code.toByte(),
            '5'.code.toByte(),
            '6'.code.toByte(),
            '7'.code.toByte(),
            '8'.code.toByte(),
            '9'.code.toByte(),
            'A'.code.toByte(),
            'B'.code.toByte(),
            'C'.code.toByte(),
            'D'.code.toByte(),
            'E'.code.toByte(),
            'F'.code.toByte(),
            'G'.code.toByte(),
            'H'.code.toByte(),
            'I'.code.toByte(),
            'J'.code.toByte(),
            'K'.code.toByte(),
            'L'.code.toByte(),
            'M'.code.toByte(),
            'N'.code.toByte(),
            'O'.code.toByte(),
            'P'.code.toByte(),
            'Q'.code.toByte(),
            'R'.code.toByte(),
            'S'.code.toByte(),
            'T'.code.toByte(),
            'U'.code.toByte(),
            'V'.code.toByte()
        )

        /** Mask used to extract 5 bits, used when encoding Base32 bytes  */
        const val MASK_5BITS: Long = 0x1f

    }

    private var encodeTable: ByteArray
    private var decodeTable: ByteArray
    private var encodeSize: Int
    private var decodeSize: Int
    private var lineSeparator: ByteArray?

    init {
        if (useHex) {
            this.encodeTable = HEX_ENCODE_TABLE
            this.decodeTable = HEX_DECODE_TABLE
        } else {
            this.encodeTable = ENCODE_TABLE
            this.decodeTable = DECODE_TABLE
        }
        if (lineLength > 0) {
            if (lineSeparator == null) {
                throw IllegalArgumentException("lineLength[$lineLength] > 0, but lineSeparator is null")
            }
            // Must be done after initializing the tables
            if (containsAlphabetOrPad(lineSeparator)) {
                throw IllegalArgumentException("lineSeparator must not contain Base32 characters: [${lineSeparator.toUtf8String()}]")
            }
            this.encodeSize = BYTES_PER_ENCODED_BLOCK + lineSeparator.size
            this.lineSeparator = ByteArray(lineSeparator.size)
            lineSeparator.copyInto(this.lineSeparator!!)
        } else {
            this.encodeSize = BYTES_PER_ENCODED_BLOCK
            this.lineSeparator = null
        }
        this.decodeSize = this.encodeSize - 1
        if (isInAlphabet(pad) || pad.isWhiteSpace) {
            throw IllegalArgumentException("pad must not be in alphabet or whitespace")
        }
    }


    /**
     * <p>
     * Decodes all of the provided data, starting at inPos, for inAvail bytes. Should be called at least twice: once
     * with the data to decode, and once with inAvail set to "-1" to alert decoder that EOF has been reached. The "-1"
     * call is not necessary when decoding, but it doesn't hurt, either.
     * </p>
     * <p>
     * Ignores all non-Base32 characters. This is how chunked (e.g. 76 character) data is handled, since CR and LF are
     * silently ignored, but has implications for other bytes, too. This method subscribes to the garbage-in,
     * garbage-out philosophy: it will not check the provided data for validity.
     * </p>
     *
     * @param in
     *            byte[] array of ascii data to Base32 decode.
     * @param inPos
     *            Position to start reading data from.
     * @param inAvail
     *            Amount of bytes available from input for encoding.
     * @param context the context to be used
     *
     * Output is written to {@link Context#buffer} as 8-bit octets, using {@link Context#pos} as the buffer position
     */
    override fun decode(source: ByteArray?, inPos: Int, inAvail: Int, context: Context) {
        if (context.eof) {
            return
        }
        if (inAvail < 0) {
            context.eof = true
        }
        source!!
        var pos = inPos
        for (i in 0 until inAvail) {
            val b = source[pos++]
            if (b == pad) {
                // We're done
                context.eof = true
                break
            }
            val buffer = ensureBufferSize(decodeSize, context)!!
            if (b >= 0 && b < this.decodeTable.size) {
                val result = this.decodeTable[b.toInt()]
                if (result >= 0) {
                    context.modulus = (context.modulus + 1) % BYTES_PER_ENCODED_BLOCK
                    // collect decoded bytes
                    context.lbitWorkArea = (context.lbitWorkArea shl BITS_PER_ENCODED_BYTE) + result
                    if (context.modulus == 0) { // we can output the 5 bytes
                        buffer[context.pos++] = ((context.lbitWorkArea shr 32) and MASK_8BITS.toLong()).toByte()
                        buffer[context.pos++] = ((context.lbitWorkArea shr 24) and MASK_8BITS.toLong()).toByte()
                        buffer[context.pos++] = ((context.lbitWorkArea shr 16) and MASK_8BITS.toLong()).toByte()
                        buffer[context.pos++] = ((context.lbitWorkArea shr 8) and MASK_8BITS.toLong()).toByte()
                        buffer[context.pos++] = (context.lbitWorkArea and MASK_8BITS.toLong()).toByte()
                    }
                }
            }
        }

        // Two forms of EOF as far as Base32 decoder is concerned: actual
        // EOF (-1) and first time '=' character is encountered in stream.
        // This approach makes the '=' padding characters completely optional.
        if (context.eof && context.modulus >= 2) { // if modulus < 2, nothing to do
            val buffer = ensureBufferSize(decodeSize, context)!!

            //  we ignore partial bytes, i.e. only multiples of 8 count
            when (context.modulus) {

                2 -> {
                    // 10 bits, drop 2 and output one byte
                    buffer[context.pos++] = ((context.lbitWorkArea shr 2) and MASK_8BITS.toLong()).toByte()
                }

                3 -> {
                    // 15 bits, drop 7 and output 1 byte
                    buffer[context.pos++] = ((context.lbitWorkArea shr 7) and MASK_8BITS.toLong()).toByte()
                }

                4 -> {
                    // 20 bits = 2*8 + 4
                    context.lbitWorkArea = context.lbitWorkArea shr 4 // drop 4 bits
                    buffer[context.pos++] = ((context.lbitWorkArea shr 8) and MASK_8BITS.toLong()).toByte()
                    buffer[context.pos++] = (context.lbitWorkArea and MASK_8BITS.toLong()).toByte()
                }

                5 -> {
                    // 25bits = 3*8 + 1
                    context.lbitWorkArea = context.lbitWorkArea shr 1
                    buffer[context.pos++] = ((context.lbitWorkArea shr 16) and MASK_8BITS.toLong()).toByte()
                    buffer[context.pos++] = ((context.lbitWorkArea shr 8) and MASK_8BITS.toLong()).toByte()
                    buffer[context.pos++] = (context.lbitWorkArea and MASK_8BITS.toLong()).toByte()
                }

                6 -> {
                    // 30bits = 3*8 + 6
                    context.lbitWorkArea = context.lbitWorkArea shr 6
                    buffer[context.pos++] = ((context.lbitWorkArea shr 16) and MASK_8BITS.toLong()).toByte()
                    buffer[context.pos++] = ((context.lbitWorkArea shr 8) and MASK_8BITS.toLong()).toByte()
                    buffer[context.pos++] = (context.lbitWorkArea and MASK_8BITS.toLong()).toByte()
                }

                7 -> {
                    // 35 = 4*8 +3
                    context.lbitWorkArea = context.lbitWorkArea shr 3
                    buffer[context.pos++] = ((context.lbitWorkArea shr 24) and MASK_8BITS.toLong()).toByte()
                    buffer[context.pos++] = ((context.lbitWorkArea shr 16) and MASK_8BITS.toLong()).toByte()
                    buffer[context.pos++] = ((context.lbitWorkArea shr 8) and MASK_8BITS.toLong()).toByte()
                    buffer[context.pos++] = (context.lbitWorkArea and MASK_8BITS.toLong()).toByte()
                }

                else -> error("Impossible modulus ${context.modulus}")
            }
        }
    }

    /**
     * <p>
     * Encodes all of the provided data, starting at inPos, for inAvail bytes. Must be called at least twice: once with
     * the data to encode, and once with inAvail set to "-1" to alert encoder that EOF has been reached, so flush last
     * remaining bytes (if not multiple of 5).
     * </p>
     *
     * @param source
     *            byte[] array of binary data to Base32 encode.
     * @param inPos
     *            Position to start reading data from.
     * @param inAvail
     *            Amount of bytes available from input for encoding.
     * @param context the context to be used
     */
    override fun encode(source: ByteArray?, inPos: Int, inAvail: Int, context: Context) {
        if (context.eof) {
            return
        }
        var pos = inPos

        // inAvail < 0 is how we're informed of EOF in the underlying data we're encoding.
        if (inAvail < 0) {
            context.eof = true
            if (context.modulus == 0 && lineLength == 0) {
                return   // no leftovers to process and not using chunking
            }
            val buffer = ensureBufferSize(encodeSize, context)!!
            val savedPos = context.pos

            when (context.modulus) {
                0 -> {
                    // Nothing to do
                }

                1 -> {
                    // Only 1 octet; take top 5 bits then remainder
                    buffer[context.pos++] =
                        encodeTable[((context.lbitWorkArea shr 3) and MASK_5BITS).toInt()] // 8-1*5 = 3
                    buffer[context.pos++] = encodeTable[((context.lbitWorkArea shl 2) and MASK_5BITS).toInt()] // 5-3=2
                    buffer[context.pos++] = pad
                    buffer[context.pos++] = pad
                    buffer[context.pos++] = pad
                    buffer[context.pos++] = pad
                    buffer[context.pos++] = pad
                    buffer[context.pos++] = pad
                }

                2 -> {
                    // 2 octets = 16 bits to use
                    buffer[context.pos++] =
                        encodeTable[((context.lbitWorkArea shr 11) and MASK_5BITS).toInt()] // 16-1*5=11
                    buffer[context.pos++] =
                        encodeTable[((context.lbitWorkArea shr 6) and MASK_5BITS).toInt()] // 16-2*5=6
                    buffer[context.pos++] =
                        encodeTable[((context.lbitWorkArea shr 1) and MASK_5BITS).toInt()] // 16-3*5=1
                    buffer[context.pos++] =
                        encodeTable[((context.lbitWorkArea shl 4) and MASK_5BITS).toInt()] // 5-1=4
                    buffer[context.pos++] = pad
                    buffer[context.pos++] = pad
                    buffer[context.pos++] = pad
                    buffer[context.pos++] = pad
                }

                3 -> {
                    // 3 octets = 24 bits to use
                    buffer[context.pos++] =
                        encodeTable[((context.lbitWorkArea shr 19) and MASK_5BITS).toInt()] // 24-1*5=19
                    buffer[context.pos++] =
                        encodeTable[((context.lbitWorkArea shr 14) and MASK_5BITS).toInt()] // 24-2*5=14
                    buffer[context.pos++] =
                        encodeTable[((context.lbitWorkArea shr 9) and MASK_5BITS).toInt()] // 24-3*5=9
                    buffer[context.pos++] =
                        encodeTable[((context.lbitWorkArea shr 4) and MASK_5BITS).toInt()] // 24-4*5=4
                    buffer[context.pos++] =
                        encodeTable[((context.lbitWorkArea shl 1) and MASK_5BITS).toInt()] // 5-1=4
                    buffer[context.pos++] = pad
                    buffer[context.pos++] = pad
                    buffer[context.pos++] = pad
                }

                4 -> {
                    // 4 octets = 32 bits to use
                    buffer[context.pos++] =
                        encodeTable[((context.lbitWorkArea shr 27) and MASK_5BITS).toInt()] // 32-1*5=27
                    buffer[context.pos++] =
                        encodeTable[((context.lbitWorkArea shr 22) and MASK_5BITS).toInt()] // 32-2*5=22
                    buffer[context.pos++] =
                        encodeTable[((context.lbitWorkArea shr 17) and MASK_5BITS).toInt()] // 32-3*5=17
                    buffer[context.pos++] =
                        encodeTable[((context.lbitWorkArea shr 12) and MASK_5BITS).toInt()] // 32-4*5=12
                    buffer[context.pos++] =
                        encodeTable[((context.lbitWorkArea shr 7) and MASK_5BITS).toInt()] // 32-5*5=7
                    buffer[context.pos++] =
                        encodeTable[((context.lbitWorkArea shr 2) and MASK_5BITS).toInt()] // 32-6*5=2
                    buffer[context.pos++] =
                        encodeTable[((context.lbitWorkArea shl 3) and MASK_5BITS).toInt()] // 5-2=3
                    buffer[context.pos++] = pad
                }

                else -> error("Impossible modulus ${context.modulus}")
            }
            context.currentLinePos += context.pos - savedPos // keep track of current line position
            // if currentPos == 0 we are at the start of a line, so don't add CRLF
            if (lineLength > 0 && context.currentLinePos > 0) {  // add chunk separator if required
                lineSeparator!!.copyInto(buffer, context.pos, 0, lineSeparator!!.size)
                context.pos += lineSeparator!!.size
            }
        } else {
            val input = source!!
            for (i in 0 until inAvail) {
                val buffer = ensureBufferSize(encodeSize, context)!!
                context.modulus = (context.modulus + 1) % BYTES_PER_UNENCODED_BLOCK
                var b = input[pos++].toInt()
                if (b < 0) {
                    b += 256
                }
                context.lbitWorkArea = (context.lbitWorkArea shl 8) + b  // BITS_PER_BYTE
                if (context.modulus == 0) {  // we have enough bytes to create our output
                    buffer[context.pos++] = encodeTable[((context.lbitWorkArea shr 35) and MASK_5BITS).toInt()]
                    buffer[context.pos++] = encodeTable[((context.lbitWorkArea shr 30) and MASK_5BITS).toInt()]
                    buffer[context.pos++] = encodeTable[((context.lbitWorkArea shr 25) and MASK_5BITS).toInt()]
                    buffer[context.pos++] = encodeTable[((context.lbitWorkArea shr 20) and MASK_5BITS).toInt()]
                    buffer[context.pos++] = encodeTable[((context.lbitWorkArea shr 15) and MASK_5BITS).toInt()]
                    buffer[context.pos++] = encodeTable[((context.lbitWorkArea shr 10) and MASK_5BITS).toInt()]
                    buffer[context.pos++] = encodeTable[((context.lbitWorkArea shr 5) and MASK_5BITS).toInt()]
                    buffer[context.pos++] = encodeTable[(context.lbitWorkArea and MASK_5BITS).toInt()]
                    context.currentLinePos += BYTES_PER_ENCODED_BLOCK
                    if (lineLength > 0 && lineLength <= context.currentLinePos) {
                        lineSeparator!!.copyInto(buffer, context.pos, 0, lineSeparator!!.size)
                        context.pos += lineSeparator!!.size
                        context.currentLinePos = 0
                    }
                }
            }
        }
    }

    override fun isInAlphabet(value: Byte): Boolean {
        return value >= 0 && value < decodeTable.size && decodeTable[value.toInt()].toInt() != -1
    }
}
