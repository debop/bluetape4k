package io.bluetape4k.tokenizer.utils

import io.bluetape4k.support.assertGe
import io.bluetape4k.support.assertZeroOrPositiveNumber
import io.bluetape4k.support.requireInRange
import java.io.Reader
import java.io.Serializable

/**
 * [CharacterUtils] provides a unified interface to Character-related operations.
 */
abstract class CharacterUtils: Serializable {

    companion object {
        @JvmStatic
        private val JAVA_5: CharacterUtils = Java5CharacterUtils()

        @JvmStatic
        fun getInstance(): CharacterUtils = JAVA_5

        @JvmStatic
        fun newCharacterBuffer(bufferSize: Int): CharacterBuffer {
            assert(bufferSize >= 2) { "buffer size must be >= 2" }
            return CharacterBuffer(CharArray(bufferSize), 0, 0)
        }

        @JvmStatic
        fun readFully(reader: Reader, dest: CharArray, offset: Int, len: Int): Int {
            var read = 0
            while (read < len) {
                val r = reader.read(dest, offset + read, len - read)
                if (r == -1) {
                    break
                }
                read += r
            }
            return read
        }
    }

    /**
     * Returns the code point at the given index of the {@link CharSequence}.
     *
     * @param seq    a character sequence
     * @param offset the offset to the char values in the chars array to be converted
     * @return the Unicode code point at the given index
     * @throws NullPointerException      - if the sequence is null.
     * @throws IndexOutOfBoundsException - if the value offset is negative or not less than the length of
     *                                   the character sequence.
     */
    abstract fun codePointAt(seq: CharSequence, offset: Int = 0): Int

    /**
     * Returns the code point at the given index of the char array where only elements
     * with index less than the limit are used.
     *
     * @param chars  a character array
     * @param offset the offset to the char values in the chars array to be converted
     * @param limit  the index afer the last element that should be used to calculate
     *               codepoint.
     * @return the Unicode code point at the given index
     * @throws NullPointerException      - if the array is null.
     * @throws IndexOutOfBoundsException - if the value offset is negative or not less than the length of
     *                                   the char array.
     */
    abstract fun codePointAt(chars: CharArray, offset: Int, limit: Int): Int

    /**
     * Return the number of characters in [seq]
     */
    abstract fun codePointCount(seq: CharSequence): Int

    fun toLowerCase(buffer: CharArray, offset: Int, limit: Int) {
        buffer.size.assertGe(limit, "buffer size")
        offset.requireInRange(0, buffer.size, "offset")

        var i = offset
        while (i < limit) {
            i += Character.toChars(Character.toLowerCase(codePointAt(buffer, i, limit)), buffer, i)
        }
    }

    fun toUpperCase(buffer: CharArray, offset: Int, limit: Int) {
        buffer.size.assertGe(limit, "buffer size")
        offset.requireInRange(0, buffer.size, "offset")

        var i = offset
        while (i < limit) {
            i += Character.toChars(Character.toUpperCase(codePointAt(buffer, i, limit)), buffer, i)
        }
    }

    fun toCodePoints(src: CharArray, srcOff: Int, srcLen: Int, dest: IntArray, destOff: Int): Int {
        srcLen.assertZeroOrPositiveNumber("srcLen")

        var codePointCount = 0
        var i = 0
        while (i < srcLen) {
            val cp = codePointAt(src, srcOff + i, srcOff + srcLen)
            val charCount = Character.charCount(cp)
            dest[destOff + codePointCount++] = cp
            i += charCount
        }
        return codePointCount
    }

    fun toChars(src: IntArray, srcOff: Int, srcLen: Int, dest: CharArray, destOff: Int): Int {
        srcLen.assertZeroOrPositiveNumber("srcLen")

        var written = 0
        for (i in 0 until srcLen) {
            written += Character.toChars(src[srcOff + i], dest, destOff + written)
        }
        return written
    }

    /**
     * Fills the {@link CharacterBuffer} with characters read from the given
     * reader {@link Reader}. This method tries to read <code>numChars</code>
     * characters into the {@link CharacterBuffer}, each call to fill will start
     * filling the buffer from offset <code>0</code> up to <code>numChars</code>.
     * In case code points can span across 2 java characters, this method may
     * only fill <code>numChars - 1</code> characters in order not to split in
     * the middle of a surrogate pair, even if there are remaining characters in
     * the {@link Reader}.
     * <p>
     * A return value of <code>false</code> means that this method call exhausted
     * the reader, but there may be some bytes which have been read, which can be
     * verified by checking whether <code>buffer.getLength() &gt; 0</code>.
     * </p>
     *
     * @param buffer   the buffer to fill.
     * @param reader   the reader to read characters from.
     * @param numChars the number of chars to read
     * @return <code>false</code> if and only if reader.read returned -1 while trying to fill the buffer
     * @throws IOException if the reader throws an {@link IOException}.
     */
    abstract fun fill(buffer: CharacterBuffer, reader: Reader, numChars: Int = buffer.buffer.size): Boolean

    /**
     * Return the index within `buf[start:start+count]` which is by `offset`
     * code points from `index`.
     */
    abstract fun offsetByCodePoints(buf: CharArray, start: Int, count: Int, index: Int, offset: Int): Int

    private class Java5CharacterUtils: CharacterUtils() {

        override fun codePointAt(seq: CharSequence, offset: Int): Int {
            return Character.codePointAt(seq, offset)
        }

        override fun codePointAt(chars: CharArray, offset: Int, limit: Int): Int {
            return Character.codePointAt(chars, offset, limit)
        }

        override fun fill(buffer: CharacterBuffer, reader: Reader, numChars: Int): Boolean {
            buffer.buffer.size.assertGe(2, "buffer size")
            numChars.requireInRange(2, buffer.buffer.size, "numChars")
            // assert(numChars in 2..buffer.buffer.size) { "numCharrs must be 2 .. buffer size" }

            val charBuffer = buffer.buffer
            buffer.offset = 0

            // Install the previously saved ending high surrogate:
            val offset = if (buffer.lastTrailingHighSurrogate != 0.toChar()) {
                charBuffer[0] = buffer.lastTrailingHighSurrogate
                buffer.lastTrailingHighSurrogate = 0.toChar()
                1
            } else {
                0
            }

            val read = readFully(reader, charBuffer, offset, numChars - offset)

            buffer.length = offset + read
            val result = buffer.length == numChars
            if (buffer.length < numChars) {
                // We failed to fill the buffer. Even if the last char is a high
                // surrogate, there is nothing we can do
                return result
            }

            if (Character.isHighSurrogate(charBuffer[buffer.length - 1])) {
                buffer.lastTrailingHighSurrogate = charBuffer[--buffer.length]
            }

            return result
        }

        override fun codePointCount(seq: CharSequence): Int {
            return Character.codePointCount(seq, 0, seq.length)
        }

        override fun offsetByCodePoints(buf: CharArray, start: Int, count: Int, index: Int, offset: Int): Int {
            return Character.offsetByCodePoints(buf, start, count, index, offset)
        }
    }

    private class Java4CharacterUtils: CharacterUtils() {

        override fun codePointAt(seq: CharSequence, offset: Int): Int {
            return seq[offset].code
        }

        override fun codePointAt(chars: CharArray, offset: Int, limit: Int): Int {
            require(offset < limit) { "offset[$offset] must be less than limit[$limit]" }
            return chars[offset].code
        }

        override fun fill(buffer: CharacterBuffer, reader: Reader, numChars: Int): Boolean {
            assert(buffer.buffer.size >= 1)
            require(numChars in 1..buffer.buffer.size) {
                "numChars must be 1 .. the buffer size[${buffer.buffer.size}]"
            }

            buffer.offset = 0
            val read = readFully(reader, buffer.buffer, 0, numChars)
            buffer.length = read
            buffer.lastTrailingHighSurrogate = 0.toChar()
            return read == numChars
        }

        override fun codePointCount(seq: CharSequence): Int {
            return seq.length
        }

        override fun offsetByCodePoints(buf: CharArray, start: Int, count: Int, index: Int, offset: Int): Int {
            val result = index + offset
            check(result in 0..count) { "index[$index]+offset[$offset] must be 0 .. count[$count]" }
            return result
        }

    }

    class CharacterBuffer private constructor(
        val buffer: CharArray,
    ) {

        companion object {
            @JvmStatic
            operator fun invoke(buffer: CharArray, offset: Int = 0, length: Int = 0): CharacterBuffer {
                return CharacterBuffer(buffer).apply {
                    this.offset = offset
                    this.length = length
                }
            }
        }

        var offset: Int = 0
            internal set
        var length: Int = 0
            internal set

        var lastTrailingHighSurrogate: Char = 0.toChar()

        fun reset() {
            offset = 0
            length = 0
            lastTrailingHighSurrogate = 0.toChar()
        }
    }
}
