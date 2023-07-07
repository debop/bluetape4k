package io.bluetape4k.multibase.binary

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.toUtf8Bytes
import io.bluetape4k.support.toUtf8String
import java.math.BigInteger

/**
 * Provides Base64 encoding and decoding as defined by <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045</a>.
 *
 * From https://commons.apache.org/proper/commons-codec/
 *
 * <p>
 * This class implements section <cite>6.8. Base64 Content-Transfer-Encoding</cite> from RFC 2045 <cite>Multipurpose
 * Internet Mail Extensions (MIME) Part One: Format of Internet Message Bodies</cite> by Freed and Borenstein.
 * </p>
 * <p>
 * The class can be parameterized in the following manner with various constructors:
 * </p>
 * <ul>
 * <li>URL-safe mode: Default off.</li>
 * <li>Line length: Default 76. Line length that aren't multiples of 4 will still essentially end up being multiples of
 * 4 in the encoded data.
 * <li>Line separator: Default is CRLF ("\r\n")</li>
 * </ul>
 * <p>
 * The URL-safe parameter is only applied to encode operations. Decoding seamlessly handles both modes.
 * </p>
 * <p>
 * Since this class operates directly on byte streams, and not character streams, it is hard-coded to only
 * encode/decode character encodings which are compatible with the lower 127 ASCII chart (ISO-8859-1, Windows-1252,
 * UTF-8, etc).
 * </p>
 * <p>
 * This class is thread-safe.
 * </p>
 *
 * @see <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045</a>
 * @since 1.0
 * @version $Id$
 */
class Base64(
    lineLength: Int = MIME_CHUNK_SIZE,
    lineSeparator: ByteArray? = CHUNK_SEPARATOR,
    urlSafe: Boolean = false,
): BaseNCodec(BYTES_PER_UNENCODED_BLOCK, BYTES_PER_ENCODED_BLOCK, lineLength, lineSeparator?.size ?: 0) {

    companion object: KLogging() {

        /**
         * BASE32 characters are 6 bits in length.
         * They are formed by taking a block of 3 octets to form a 24-bit string,
         * which is converted into 4 BASE64 characters.
         */
        private const val BITS_PER_ENCODED_BYTE = 6
        private const val BYTES_PER_UNENCODED_BLOCK = 3
        private const val BYTES_PER_ENCODED_BLOCK = 4

        /**
         * Chunk separator per RFC 2045 section 2.1.
         *
         *
         *
         * N.B. The next major release may break compatibility and make this field private.
         *
         *
         * @see [RFC 2045 section 2.1](http://www.ietf.org/rfc/rfc2045.txt)
         */
        val CHUNK_SEPARATOR = byteArrayOf('\r'.code.toByte(), '\n'.code.toByte())

        /**
         * This array is a lookup table that translates 6-bit positive integer index values into their "Base64 Alphabet"
         * equivalents as specified in Table 1 of RFC 2045.
         *
         * Thanks to "commons" project in ws.apache.org for this code.
         * http://svn.apache.org/repos/asf/webservices/commons/trunk/modules/util/
         */
        private val STANDARD_ENCODE_TABLE = byteArrayOf(
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
            'a'.code.toByte(),
            'b'.code.toByte(),
            'c'.code.toByte(),
            'd'.code.toByte(),
            'e'.code.toByte(),
            'f'.code.toByte(),
            'g'.code.toByte(),
            'h'.code.toByte(),
            'i'.code.toByte(),
            'j'.code.toByte(),
            'k'.code.toByte(),
            'l'.code.toByte(),
            'm'.code.toByte(),
            'n'.code.toByte(),
            'o'.code.toByte(),
            'p'.code.toByte(),
            'q'.code.toByte(),
            'r'.code.toByte(),
            's'.code.toByte(),
            't'.code.toByte(),
            'u'.code.toByte(),
            'v'.code.toByte(),
            'w'.code.toByte(),
            'x'.code.toByte(),
            'y'.code.toByte(),
            'z'.code.toByte(),
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
            '+'.code.toByte(),
            '/'
                .code.toByte()
        )

        /**
         * This is a copy of the STANDARD_ENCODE_TABLE above, but with + and /
         * changed to - and _ to make the encoded Base64 results more URL-SAFE.
         * This table is only used when the Base64's mode is set to URL-SAFE.
         */
        private val URL_SAFE_ENCODE_TABLE = byteArrayOf(
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
            'a'.code.toByte(),
            'b'.code.toByte(),
            'c'.code.toByte(),
            'd'.code.toByte(),
            'e'.code.toByte(),
            'f'.code.toByte(),
            'g'.code.toByte(),
            'h'.code.toByte(),
            'i'.code.toByte(),
            'j'.code.toByte(),
            'k'.code.toByte(),
            'l'.code.toByte(),
            'm'.code.toByte(),
            'n'.code.toByte(),
            'o'.code.toByte(),
            'p'.code.toByte(),
            'q'.code.toByte(),
            'r'.code.toByte(),
            's'.code.toByte(),
            't'.code.toByte(),
            'u'.code.toByte(),
            'v'.code.toByte(),
            'w'.code.toByte(),
            'x'.code.toByte(),
            'y'.code.toByte(),
            'z'.code.toByte(),
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
            '-'.code.toByte(),
            '_'
                .code.toByte()
        )

        /**
         * This array is a lookup table that translates Unicode characters drawn from the "Base64 Alphabet" (as specified
         * in Table 1 of RFC 2045) into their 6-bit positive integer equivalents. Characters that are not in the Base64
         * alphabet but fall within the bounds of the array are translated to -1.
         *
         * Note: '+' and '-' both decode to 62. '/' and '_' both decode to 63. This means decoder seamlessly handles both
         * URL_SAFE and STANDARD base64. (The encoder, on the other hand, needs to know ahead of time what to emit).
         *
         * Thanks to "commons" project in ws.apache.org for this code.
         * http://svn.apache.org/repos/asf/webservices/commons/trunk/modules/util/
         */
        private val DECODE_TABLE = byteArrayOf( //   0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  // 00-0f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  // 10-1f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63,  // 20-2f + - /
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,  // 30-3f 0-9
            -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,  // 40-4f A-O
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63,  // 50-5f P-Z _
            -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,  // 60-6f a-o
            41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51 // 70-7a p-z
        )

        /**
         * Base64 uses 6-bit fields.
         */
        /**
         * Base64 uses 6-bit fields.
         */
        /** Mask used to extract 6 bits, used when encoding  */
        private const val MASK_6BITS = 0x3f


        /**
         * Returns whether or not the `octet` is in the base 64 alphabet.
         *
         * @param octet
         * The value to test
         * @return `true` if the value is defined in the the base 64 alphabet, `false` otherwise.
         * @since 1.4
         */
        fun isBase64(octet: Byte): Boolean {
            return octet == PAD_DEFAULT || octet >= 0 &&
                    octet < DECODE_TABLE.size &&
                    DECODE_TABLE[octet.toInt()].toInt() != -1
        }

        /**
         * Tests a given String to see if it contains only valid characters within the Base64 alphabet. Currently the
         * method treats whitespace as valid.
         *
         * @param base64
         * String to test
         * @return `true` if all characters in the String are valid characters in the Base64 alphabet or if
         * the String is empty; `false`, otherwise
         */
        fun isBase64(base64: String?): Boolean {
            return base64?.toUtf8Bytes()?.let { isBase64(it) } ?: false
        }

        /**
         * Tests a given byte array to see if it contains only valid characters within the Base64 alphabet. Currently the
         * method treats whitespace as valid.
         *
         * @param arrayOctet
         * byte array to test
         * @return `true` if all bytes are valid characters in the Base64 alphabet or if the byte array is empty;
         * `false`, otherwise
         */
        fun isBase64(arrayOctet: ByteArray): Boolean {
            for (i in arrayOctet.indices) {
                if (!isBase64(arrayOctet[i]) && !arrayOctet[i].isWhiteSpace) {
                    return false
                }
            }
            return true
        }

        /**
         * Encodes binary data using the base64 algorithm but does not chunk the output.
         *
         * @param binaryData
         * binary data to encode
         * @return byte[] containing Base64 characters in their UTF-8 representation.
         */
        fun encodeBase64(binaryData: ByteArray): ByteArray {
            return encodeBase64(binaryData, false)
        }

        /**
         * Encodes binary data using the base64 algorithm but does not chunk the output.
         *
         * NOTE:  We changed the behaviour of this method from multi-line chunking (commons-codec-1.4) to
         * single-line non-chunking (commons-codec-1.5).
         *
         * @param binaryData
         * binary data to encode
         * @return String containing Base64 characters.
         * @since 1.4 (NOTE:  1.4 chunked the output, whereas 1.5 does not).
         */
        fun encodeBase64String(binaryData: ByteArray): String {
            return encodeBase64(binaryData, isChunked = false).toString(Charsets.US_ASCII)
        }

        /**
         * Encodes binary data using a URL-safe variation of the base64 algorithm but does not chunk the output. The
         * url-safe variation emits - and _ instead of + and / characters.
         * **Note: no padding is added.**
         * @param binaryData
         * binary data to encode
         * @return byte[] containing Base64 characters in their UTF-8 representation.
         * @since 1.4
         */
        fun encodeBase64URLSafe(binaryData: ByteArray): ByteArray {
            return encodeBase64(binaryData, isChunked = false, urlSafe = true)
        }

        /**
         * Encodes binary data using a URL-safe variation of the base64 algorithm but does not chunk the output. The
         * url-safe variation emits - and _ instead of + and / characters.
         * **Note: no padding is added.**
         * @param binaryData
         * binary data to encode
         * @return String containing Base64 characters
         * @since 1.4
         */
        fun encodeBase64URLSafeString(binaryData: ByteArray): String {
            return encodeBase64(binaryData, isChunked = false, urlSafe = true).toString(Charsets.US_ASCII)
        }

        /**
         * Encodes binary data using the base64 algorithm and chunks the encoded output into 76 character blocks
         *
         * @param binaryData
         * binary data to encode
         * @return Base64 characters chunked in 76 character blocks
         */
        fun encodeBase64Chunked(binaryData: ByteArray): ByteArray? {
            return encodeBase64(binaryData, isChunked = true)
        }

        /**
         * Encodes binary data using the base64 algorithm, optionally chunking the output into 76 character blocks.
         *
         * @param binaryData
         * Array containing binary data to encode.
         * @param isChunked
         * if `true` this encoder will chunk the base64 output into 76 character blocks
         * @return Base64-encoded data.
         * @throws IllegalArgumentException
         * Thrown when the input array needs an output array bigger than [Integer.MAX_VALUE]
         */
        fun encodeBase64(binaryData: ByteArray, isChunked: Boolean): ByteArray {
            return encodeBase64(binaryData, isChunked, urlSafe = false)
        }

        /**
         * Encodes binary data using the base64 algorithm, optionally chunking the output into 76 character blocks.
         *
         * @param binaryData
         * Array containing binary data to encode.
         * @param isChunked
         * if `true` this encoder will chunk the base64 output into 76 character blocks
         * @param urlSafe
         * if `true` this encoder will emit - and _ instead of the usual + and / characters.
         * **Note: no padding is added when encoding using the URL-safe alphabet.**
         * @return Base64-encoded data.
         * @throws IllegalArgumentException
         * Thrown when the input array needs an output array bigger than [Integer.MAX_VALUE]
         * @since 1.4
         */
        fun encodeBase64(binaryData: ByteArray, isChunked: Boolean, urlSafe: Boolean): ByteArray {
            return encodeBase64(binaryData, isChunked, urlSafe, Int.MAX_VALUE)
        }

        /**
         * Encodes binary data using the base64 algorithm, optionally chunking the output into 76 character blocks.
         *
         * @param binaryData
         * Array containing binary data to encode.
         * @param isChunked
         * if `true` this encoder will chunk the base64 output into 76 character blocks
         * @param urlSafe
         * if `true` this encoder will emit - and _ instead of the usual + and / characters.
         * **Note: no padding is added when encoding using the URL-safe alphabet.**
         * @param maxResultSize
         * The maximum result size to accept.
         * @return Base64-encoded data.
         * @throws IllegalArgumentException
         * Thrown when the input array needs an output array bigger than maxResultSize
         * @since 1.4
         */
        fun encodeBase64(
            binaryData: ByteArray,
            isChunked: Boolean,
            urlSafe: Boolean,
            maxResultSize: Int,
        ): ByteArray {
            if (binaryData.size == 0) {
                return binaryData
            }

            // Create this so can use the super-class method
            // Also ensures that the same roundings are performed by the ctor and the code
            val b64: Base64 =
                if (isChunked) Base64(urlSafe = urlSafe)
                else Base64(0, CHUNK_SEPARATOR, urlSafe)

            val len: Long = b64.getEncodedLength(binaryData)
            if (len > maxResultSize) {
                throw java.lang.IllegalArgumentException(
                    "Input array too big, the output array would be bigger ($len) than the specified maximum size of $maxResultSize"
                )
            }
            return b64.encode(binaryData)
        }

        /**
         * Decodes a Base64 String into octets.
         *
         *
         * **Note:** this method seamlessly handles data encoded in URL-safe or normal mode.
         *
         *
         * @param base64String
         * String containing Base64 data
         * @return Array containing decoded data.
         */
        fun decodeBase64(base64String: String): ByteArray {
            return Base64().decode(base64String)
        }

        /**
         * Decodes Base64 data into octets.
         *
         *
         * **Note:** this method seamlessly handles data encoded in URL-safe or normal mode.
         *
         *
         * @param base64Data
         * Byte array containing Base64 data
         * @return Array containing decoded data.
         */
        fun decodeBase64(base64Data: ByteArray): ByteArray {
            return Base64().decode(base64Data)
        }

        // Implementation of the Encoder Interface

        // Implementation of integer encoding used for crypto
        // Implementation of the Encoder Interface
        // Implementation of integer encoding used for crypto
        /**
         * Decodes a byte64-encoded integer according to crypto standards such as W3C's XML-Signature.
         *
         * @param pArray
         * a byte array containing base64 character data
         * @return A BigInteger
         * @since 1.4
         */
        fun decodeInteger(pArray: ByteArray): BigInteger {
            return BigInteger(1, decodeBase64(pArray))
        }

        /**
         * Encodes to a byte64-encoded integer according to crypto standards such as W3C's XML-Signature.
         *
         * @param bigInt
         * a BigInteger
         * @return A byte array containing base64 character data
         * @throws NullPointerException
         * if null is passed in
         * @since 1.4
         */
        fun encodeInteger(bigInt: BigInteger): ByteArray {
            return encodeBase64(toIntegerBytes(bigInt), false)
        }

        /**
         * Returns a byte-array representation of a `BigInteger` without sign bit.
         *
         * @param bigInt
         * `BigInteger` to be converted
         * @return a byte array representation of the BigInteger parameter
         */
        fun toIntegerBytes(bigInt: BigInteger): ByteArray {
            var bitlen = bigInt.bitLength()
            // round bitlen
            bitlen = ((bitlen + 7) shr 3) shl 3
            val bigBytes = bigInt.toByteArray()
            if (((bigInt.bitLength() % 8) != 0) && (((bigInt.bitLength() / 8) + 1) == (bitlen / 8))) {
                return bigBytes
            }
            // set up params for copying everything but sign bit
            var startSrc = 0
            var len = bigBytes.size

            // if bigInt is exactly byte-aligned, just skip signbit in copy
            if ((bigInt.bitLength() % 8) == 0) {
                startSrc = 1
                len--
            }
            val startDst = bitlen / 8 - len // to pad w/ nulls as per spec
            val resizedBytes = ByteArray(bitlen / 8)
            System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, len)
            return resizedBytes
        }
    }

    private var encodeTable: ByteArray
    private var decodeTable: ByteArray = DECODE_TABLE
    private var encodeSize: Int
    private var decodeSize: Int
    private var lineSeparator: ByteArray?

    val isUrlSafe: Boolean get() = (this.encodeTable === URL_SAFE_ENCODE_TABLE)

    init {
        // TODO could be simplified if there is no requirement to reject invalid line sep when length <=0
        // @see test case Base64Test.testConstructors()
        if (lineSeparator != null) {
            if (containsAlphabetOrPad(lineSeparator)) {
                throw IllegalArgumentException("lineSeparator must not contain base64 characters: [${lineSeparator.toUtf8String()}]")
            }
            if (lineLength > 0) { // null line-sep forces no chunking rather than throwing IAE
                encodeSize = BYTES_PER_ENCODED_BLOCK + lineSeparator.size
                this.lineSeparator = ByteArray(lineSeparator.size)
                lineSeparator.copyInto(this.lineSeparator!!)
            } else {
                encodeSize = BYTES_PER_ENCODED_BLOCK
                this.lineSeparator = null
            }
        } else {
            this.encodeSize = BYTES_PER_ENCODED_BLOCK
            this.lineSeparator = null
        }
        this.decodeSize = encodeSize - 1
        this.encodeTable = if (urlSafe) URL_SAFE_ENCODE_TABLE else STANDARD_ENCODE_TABLE
    }


    /**
     *
     *
     * Encodes all of the provided data, starting at inPos, for inAvail bytes. Must be called at least twice: once with
     * the data to encode, and once with inAvail set to "-1" to alert encoder that EOF has been reached, to flush last
     * remaining bytes (if not multiple of 3).
     *
     *
     * **Note: no padding is added when encoding using the URL-safe alphabet.**
     *
     *
     * Thanks to "commons" project in ws.apache.org for the bitwise operations, and general approach.
     * http://svn.apache.org/repos/asf/webservices/commons/trunk/modules/util/
     *
     *
     * @param `in`
     * byte[] array of binary data to base64 encode.
     * @param inPos
     * Position to start reading data from.
     * @param inAvail
     * Amount of bytes available from input for encoding.
     * @param context
     * the context to be used
     */
    override fun encode(source: ByteArray, inPos: Int, inAvail: Int, context: Context) {
        var pos = inPos
        if (context.eof) {
            return
        }
        // inAvail < 0 is how we're informed of EOF in the underlying data we're encoding.
        if (inAvail < 0) {
            context.eof = true
            if (0 == context.modulus && lineLength == 0) {
                return  // no leftovers to process and not using chunking
            }
            val buffer = ensureBufferSize(encodeSize, context)!!
            val savedPos = context.pos
            when (context.modulus) {
                0 -> {}
                1 -> {
                    // top 6 bits:
                    buffer[context.pos++] = encodeTable[(context.ibitWorkArea shr 2) and MASK_6BITS]
                    // remaining 2:
                    buffer[context.pos++] = encodeTable[(context.ibitWorkArea shl 4) and MASK_6BITS]
                    // URL-SAFE skips the padding to further reduce size.
                    if (!isUrlSafe) {
                        buffer[context.pos++] = pad
                        buffer[context.pos++] = pad
                    }
                }

                2 -> {
                    buffer[context.pos++] = encodeTable[(context.ibitWorkArea shr 10) and MASK_6BITS]
                    buffer[context.pos++] = encodeTable[(context.ibitWorkArea shr 4) and MASK_6BITS]
                    buffer[context.pos++] = encodeTable[(context.ibitWorkArea shl 2) and MASK_6BITS]
                    // URL-SAFE skips the padding to further reduce size.
                    if (!isUrlSafe) {
                        buffer[context.pos++] = pad
                    }
                }

                else -> error("Impossible modulus ${context.modulus}")
            }

            context.currentLinePos += context.pos - savedPos // keep track of current line position
            // if currentPos == 0 we are at the start of a line, so don't add CRLF
            if (lineLength > 0 && context.currentLinePos > 0) {
                lineSeparator!!.copyInto(buffer, 0, context.pos, lineSeparator!!.size)
                context.pos += lineSeparator!!.size
            }
        } else {
            for (i in 0 until inAvail) {
                val buffer = ensureBufferSize(encodeSize, context)!!
                context.modulus = (context.modulus + 1) % BYTES_PER_UNENCODED_BLOCK
                var b = source[pos++].toInt()
                if (b < 0) {
                    b += 256
                }
                context.ibitWorkArea = (context.ibitWorkArea shl 8) + b //  BITS_PER_BYTE
                if (0 == context.modulus) { // 3 bytes = 24 bits = 4 * 6 bits to extract
                    buffer[context.pos++] = encodeTable[(context.ibitWorkArea shr 18) and MASK_6BITS]
                    buffer[context.pos++] = encodeTable[(context.ibitWorkArea shr 12) and MASK_6BITS]
                    buffer[context.pos++] = encodeTable[(context.ibitWorkArea shr 6) and MASK_6BITS]
                    buffer[context.pos++] = encodeTable[context.ibitWorkArea and MASK_6BITS]
                    context.currentLinePos += BYTES_PER_ENCODED_BLOCK
                    if (lineLength > 0 && lineLength <= context.currentLinePos) {
                        lineSeparator!!.copyInto(buffer, 0, context.pos, lineSeparator!!.size)
                        context.pos += lineSeparator!!.size
                        context.currentLinePos = 0
                    }
                }
            }
        }
    }

    /**
     *
     *
     * Decodes all of the provided data, starting at inPos, for inAvail bytes. Should be called at least twice: once
     * with the data to decode, and once with inAvail set to "-1" to alert decoder that EOF has been reached. The "-1"
     * call is not necessary when decoding, but it doesn't hurt, either.
     *
     *
     *
     * Ignores all non-base64 characters. This is how chunked (e.g. 76 character) data is handled, since CR and LF are
     * silently ignored, but has implications for other bytes, too. This method subscribes to the garbage-in,
     * garbage-out philosophy: it will not check the provided data for validity.
     *
     *
     *
     * Thanks to "commons" project in ws.apache.org for the bitwise operations, and general approach.
     * http://svn.apache.org/repos/asf/webservices/commons/trunk/modules/util/
     *
     *
     * @param `in`
     * byte[] array of ascii data to base64 decode.
     * @param inPos
     * Position to start reading data from.
     * @param inAvail
     * Amount of bytes available from input for encoding.
     * @param context
     * the context to be used
     */
    override fun decode(source: ByteArray, inPos: Int, inAvail: Int, context: Context) {
        var pos = inPos
        if (context.eof) {
            return
        }
        if (inAvail < 0) {
            context.eof = true
        }

        for (i in 0 until inAvail) {
            val buffer = ensureBufferSize(decodeSize, context)!!
            val b = source[pos++]
            if (b == pad) {
                // We're done.
                context.eof = true
                break
            }
            if (b >= 0 && b < DECODE_TABLE.size) {
                val result: Int = DECODE_TABLE.get(b.toInt()).toInt()
                if (result >= 0) {
                    context.modulus = (context.modulus + 1) % BYTES_PER_ENCODED_BLOCK
                    context.ibitWorkArea =
                        (context.ibitWorkArea shl BITS_PER_ENCODED_BYTE) + result
                    if (context.modulus == 0) {
                        buffer[context.pos++] = (context.ibitWorkArea shr 16 and MASK_8BITS).toByte()
                        buffer[context.pos++] = (context.ibitWorkArea shr 8 and MASK_8BITS).toByte()
                        buffer[context.pos++] = (context.ibitWorkArea and MASK_8BITS).toByte()
                    }
                }
            }
        }

        // Two forms of EOF as far as base64 decoder is concerned: actual
        // EOF (-1) and first time '=' character is encountered in stream.
        // This approach makes the '=' padding characters completely optional.
        if (context.eof && context.modulus != 0) {
            val buffer = ensureBufferSize(decodeSize, context)!!
            when (context.modulus) {
                1    -> {}
                2    -> {
                    context.ibitWorkArea = context.ibitWorkArea shr 4 // dump the extra 4 bits
                    buffer[context.pos++] = (context.ibitWorkArea and MASK_8BITS).toByte()
                }

                3    -> {
                    context.ibitWorkArea = context.ibitWorkArea shr 2 // dump 2 bits
                    buffer[context.pos++] = (context.ibitWorkArea shr 8 and MASK_8BITS).toByte()
                    buffer[context.pos++] = (context.ibitWorkArea and MASK_8BITS).toByte()
                }

                else -> error("Impossible modulus ${context.modulus}")
            }
        }
    }


    /**
     * Returns whether or not the `octet` is in the Base64 alphabet.
     *
     * @param octet The value to test
     *
     * @return `true` if the value is defined in the the Base64 alphabet `false` otherwise.
     */
    override fun isInAlphabet(octet: Byte): Boolean {
        return (octet >= 0) && (octet < decodeTable.size) && (decodeTable[octet.toInt()].toInt() != -1)
    }
}
