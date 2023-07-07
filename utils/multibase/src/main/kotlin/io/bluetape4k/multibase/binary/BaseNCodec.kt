package io.bluetape4k.multibase.binary

import io.bluetape4k.logging.KLogging
import io.bluetape4k.multibase.BinaryDecoder
import io.bluetape4k.multibase.BinaryEncoder
import io.bluetape4k.multibase.exceptions.DecoderException
import io.bluetape4k.support.isNullOrEmpty
import io.bluetape4k.support.toUtf8Bytes
import io.bluetape4k.support.toUtf8String
import org.apache.commons.codec.EncoderException

abstract class BaseNCodec(
    val unencodedBlockSize: Int,
    val encodedBlockSize: Int,
    lineSize: Int,
    val chunkSeparatorLength: Int,
    val pad: Byte = PAD_DEFAULT,
): BinaryEncoder, BinaryDecoder {


    /**
     * Holds thread context so classes can be thread-safe.
     *
     * This class is not itself thread-safe; each thread must allocate its own copy.
     *
     * @since 1.7
     */
    class Context {
        /**
         * Place holder for the bytes we're dealing with for our based logic.
         * Bitwise operations store and extract the encoding or decoding from this variable.
         */
        var ibitWorkArea = 0

        /**
         * Place holder for the bytes we're dealing with for our based logic.
         * Bitwise operations store and extract the encoding or decoding from this variable.
         */
        var lbitWorkArea: Long = 0

        /**
         * Buffer for streaming.
         */
        var buffer: ByteArray? = null

        /**
         * Position where next character should be written in the buffer.
         */
        var pos = 0

        /**
         * Position where next character should be read from the buffer.
         */
        var readPos = 0

        /**
         * Boolean flag to indicate the EOF has been reached. Once EOF has been reached, this object becomes useless,
         * and must be thrown away.
         */
        var eof = false

        /**
         * Variable tracks how many characters have been written to the current line. Only used when encoding. We use
         * it to make sure each encoded line never goes beyond lineLength (if lineLength &gt; 0).
         */
        var currentLinePos = 0

        /**
         * Writes to the buffer only occur after every 3/5 reads when encoding, and every 4/8 reads when decoding. This
         * variable helps track that.
         */
        var modulus = 0
    }

    companion object: KLogging() {

        const val EOF = -1

        /**
         * MIME chunk size per RFC 2045 section 6.8.
         *
         * The {@value} character limit does not count the trailing CRLF, but counts all other characters, including any
         * equal signs.
         *
         * @see [RFC 2045 section 6.8](http://www.ietf.org/rfc/rfc2045.txt)
         */
        const val MIME_CHUNK_SIZE = 76

        /**
         * PEM chunk size per RFC 1421 section 4.3.2.4.
         *
         * The {@value} character limit does not count the trailing CRLF, but counts all other characters, including any
         * equal signs.
         *
         * @see [RFC 1421 section 4.3.2.4](http://tools.ietf.org/html/rfc1421)
         */
        const val PEM_CHUNK_SIZE = 64

        private const val DEFAULT_BUFFER_RESIZE_FACTOR = 2

        /**
         * Defines the default buffer size - currently {@value}
         * - must be large enough for at least one encoded block+separator
         */
        private const val DEFAULT_BUFFER_SIZE = 8192

        /** Mask used to extract 8 bits, used in decoding bytes  */
        const val MASK_8BITS: Int = 0xff

        /**
         * Byte used to pad output.
         */
        const val PAD_DEFAULT = '='.code.toByte() // Allow static access to default

        const val pad: Byte = 0 // instance variable just in case it needs to vary later


        /** Number of bytes in each full block of unencoded data, e.g. 4 for Base64 and 5 for Base32  */
        private const val unencodedBlockSize = 0

        /** Number of bytes in each full block of encoded data, e.g. 3 for Base64 and 8 for Base32  */
        private const val encodedBlockSize = 0

        /**
         * Chunksize for encoding. Not used when decoding.
         * A value of zero or less implies no chunking of the encoded data.
         * Rounded down to nearest multiple of encodedBlockSize.
         */
        protected const val lineLength = 0

        /**
         * Size of chunk separator. Not used unless [.lineLength] &gt; 0.
         */
        private const val chunkSeparatorLength = 0

        val Byte.isWhiteSpace: Boolean
            get() {
                return when (this.toInt().toChar()) {
                    ' ' -> true
                    '\n' -> true
                    '\r' -> true
                    '\t' -> true
                    else -> false
                }
            }
    }

    val lineLength =
        if (lineSize > 0 && chunkSeparatorLength > 0) (lineSize / encodedBlockSize) * encodedBlockSize
        else 0

    val Context.hasData: Boolean get() = buffer != null
    val Context.available: Int get() = if (hasData) pos - readPos else 0

    protected val defaultBufferSize: Int get() = DEFAULT_BUFFER_SIZE

    /**
     * Increases our buffer by the {@link #DEFAULT_BUFFER_RESIZE_FACTOR}.
     * @param context the context to be used
     */
    private fun resizeBuffer(context: Context): ByteArray {
        val buffer = context.buffer
        if (buffer == null) {
            context.buffer = ByteArray(defaultBufferSize)
            context.pos = 0
            context.readPos = 0
        } else {
            val b = ByteArray(buffer.size * DEFAULT_BUFFER_RESIZE_FACTOR)
            buffer.copyInto(b, 0, 0, buffer.size)
            context.buffer = b
        }
        return context.buffer!!
    }

    /**
     * Ensure that the buffer has room for [size] bytes
     *
     * @param size minimum spare space required
     * @param context the context to be used
     * @return the buffer
     */
    protected fun ensureBufferSize(size: Int, context: Context): ByteArray? {
        if ((context.buffer == null) || (context.buffer!!.size < context.pos + size)) {
            return resizeBuffer(context)
        }
        return context.buffer
    }

    /**
     * Extracts buffered data into the provided ByteArray, starting at position bPos, up to a maximum of bAvail
     * bytes. Returns how many bytes were actually extracted.
     *
     * Package protected for access from I/O streams.
     *
     * @param b
     *            ByteArray array to extract the buffered data into.
     * @param bPos
     *            position in ByteArray array to start extraction at.
     * @param bAvail
     *            amount of bytes we're allowed to extract. We may extract fewer (if fewer are available).
     * @param context
     *            the context to be used
     * @return The number of bytes successfully extracted into the provided ByteArray array.
     */
    internal fun readResults(b: ByteArray, bPos: Int, bAvail: Int, context: Context): Int {
        if (context.hasData) {
            val len = minOf(context.available, bAvail)
            context.buffer!!.copyInto(b, bPos, context.readPos, len)
            context.readPos += len
            if (context.readPos >= context.pos) {
                context.buffer = null   // so hasData() will return false, and this method can return -1
            }
            return len
        }
        return if (context.eof) EOF else 0
    }

    /**
     * Encodes an Object using the Base-N algorithm. This method is provided in order to satisfy the requirements of
     * the Encoder interface, and will throw an EncoderException if the supplied object is not of type ByteArray.
     *
     * @param source
     *            Object to encode
     * @return An object (of type ByteArray) containing the Base-N encoded data which corresponds to the ByteArray supplied.
     * @throws EncoderException
     *             if the parameter supplied is not of type ByteArray
     */
    override fun encode(source: Any?): Any? {
        if (source is ByteArray) {
            return encode(source)
        }
        throw EncoderException("Parameter supplied to Base-N encode is not a ByteArray")
    }

    /**
     * Encodes a ByteArray containing binary data, into a String containing characters in the appropriate alphabet.
     * Uses UTF8 encoding.
     *
     * @param source a byte array containing binary data
     * @return String containing only character data in the appropriate alphabet.
     * @since 1.5
     * This is a duplicate of {@link #encodeToString(ByteArray)}; it was merged during refactoring.
     */
    fun encodeAsString(source: ByteArray?): String? {
        return encode(source)?.toUtf8String()
    }

    /**
     * Decodes an Object using the Base-N algorithm. This method is provided in order to satisfy the requirements of
     * the Decoder interface, and will throw a DecoderException if the supplied object is not of type ByteArray or String.
     *
     * @param source Object to decode
     * @return An object (of type ByteArray) containing the binary data which corresponds to the ByteArray or String
     *         supplied.
     * @throws DecoderException
     *             if the parameter supplied is not of type ByteArray
     */
    override fun decode(source: Any?): Any? {
        return when (source) {
            is ByteArray -> decode(source)
            is String -> decode(source)
            else -> throw DecoderException("Parameter supplied to Base-N decode is not a ByteArray or a String")
        }
    }

    /**
     * Decodes a String containing characters in the Base-N alphabet.
     *
     * @param source A String containing Base-N character data
     * @return a byte array containing binary data
     */
    fun decode(source: String): ByteArray? {
        return decode(source.toUtf8Bytes())
    }

    /**
     * Decodes a ByteArray containing characters in the Base-N alphabet.
     *
     * @param source A byte array containing Base-N character data
     * @return a byte array containing binary data
     */
    override fun decode(source: ByteArray?): ByteArray? {
        if (source.isNullOrEmpty()) {
            return source
        }
        val context = Context()
        decode(source, 0, source!!.size, context)
        decode(source, 0, EOF, context)
        val result = ByteArray(context.pos)
        readResults(result, 0, result.size, context)
        return result
    }

    /**
     * Encodes a ByteArray containing binary data, into a ByteArray containing characters in the alphabet.
     *
     * @param source
     *            a byte array containing binary data
     * @return A byte array containing only the base N alphabetic character data
     */
    override fun encode(source: ByteArray?): ByteArray? {
        if (source.isNullOrEmpty()) {
            return source
        }
        return encode(source, 0, source!!.size)
    }

    /**
     * Encodes a ByteArray containing binary data, into a ByteArray containing
     * characters in the alphabet.
     *
     * @param source
     *            a byte array containing binary data
     * @param offset
     *            initial offset of the subarray.
     * @param length
     *            length of the subarray.
     * @return A byte array containing only the base N alphabetic character data
     */
    fun encode(source: ByteArray?, offset: Int, length: Int): ByteArray? {
        if (source.isNullOrEmpty()) {
            return source
        }
        val context = Context()
        encode(source, offset, length, context)
        encode(source, offset, EOF, context)
        val buf = ByteArray(context.pos - context.readPos)
        readResults(buf, 0, buf.size, context)
        return buf
    }

    abstract fun encode(source: ByteArray?, i: Int, length: Int, context: Context)

    abstract fun decode(source: ByteArray?, i: Int, length: Int, context: Context)

    /**
     * Returns whether or not the <code>octet</code> is in the current alphabet.
     * Does not allow whitespace or pad.
     *
     * @param value The value to test
     *
     * @return <code>true</code> if the value is defined in the current alphabet, <code>false</code> otherwise.
     */
    protected abstract fun isInAlphabet(value: Byte): Boolean

    /**
     * Tests a given byte array to see if it contains only valid characters within the alphabet.
     * The method optionally treats whitespace and pad as valid.
     *
     * @param arrayOctet byte array to test
     * @param allowWSPad if <code>true</code>, then whitespace and PAD are also allowed
     *
     * @return <code>true</code> if all bytes are valid characters in the alphabet or if the byte array is empty;
     *         <code>false</code>, otherwise
     */
    fun isInAlphabet(arrayOctet: ByteArray, allowWSPad: Boolean): Boolean {
        arrayOctet.forEach {
            if (!isInAlphabet(it) && (!allowWSPad || (it != pad) && !it.isWhiteSpace)) {
                return false
            }
        }
        return true
    }

    /**
     * Tests a given String to see if it contains only valid characters within the alphabet.
     * The method treats whitespace and PAD as valid.
     *
     * @param basen String to test
     * @return `true` if all characters in the String are valid characters in the alphabet or if
     *         the String is empty; `false`, otherwise
     * @see isInAlphabet(ByteArray, Boolean)
     */
    fun isInAlphabet(basen: String): Boolean {
        return isInAlphabet(basen.toUtf8Bytes(), true)
    }

    protected fun containsAlphabetOrPad(arrayOctet: ByteArray?): Boolean {
        if (arrayOctet == null) {
            return false
        }
        arrayOctet.forEach {
            if (it == pad || isInAlphabet(it)) {
                return true
            }
        }
        return false
    }

    /**
     * Calculates the amount of space needed to encode the supplied array.
     *
     * @param pArray ByteArray array which will later be encoded
     *
     * @return amount of space needed to encoded the supplied array.
     * Returns a long since a max-len array will require &gt; Integer.MAX_VALUE
     */
    fun getEncodedLength(pArray: ByteArray): Long {
        // Calculate non-chunked size - rounded up to allow for padding
        // cast to long is needed to avoid possibility of overflow
        var len = ((pArray.size + unencodedBlockSize - 1) / unencodedBlockSize) * encodedBlockSize.toLong()
        if (lineLength > 0) {
            len += ((len + lineLength - 1) / lineLength) * chunkSeparatorLength
        }
        return len
    }

}
