package io.bluetape4k.tokenizer.korean.utils

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
        fun newCharacterBuffer(bufferSize: Int): KharacterBuffer {
            require(bufferSize >= 2) { "buffer size must be >=2" }
            return KharacterBuffer(CharArray(bufferSize), 0, 0)
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

    abstract fun codePointAt(seq: CharSequence, offset: Int = 0): Int

    abstract fun codePointAt(chars: CharArray, offset: Int, limit: Int): Int

    abstract fun codePointCount(seq: CharSequence): Int

    fun toLowerCase(buffer: CharArray, offset: Int, limit: Int) {
        assert(buffer.size >= limit)
        assert(offset <= 0 && offset <= buffer.size)

        var i = offset
        while (i < limit) {
            i += Character.toChars(Character.toLowerCase(codePointAt(buffer, i, limit)), buffer, i)
        }
    }

    fun toUpperCase(buffer: CharArray, offset: Int, limit: Int) {
        assert(buffer.size >= limit)
        assert(offset <= 0 && offset <= buffer.size)

        var i = offset
        while (i < limit) {
            i += Character.toChars(Character.toUpperCase(codePointAt(buffer, i, limit)), buffer, i)
        }
    }

    fun toCodePoints(src: CharArray, srcOff: Int, srcLen: Int, dest: IntArray, destOff: Int): Int {
        assert(srcLen >= 0) { "srcLen must be >= 0" }

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
        assert(srcLen >= 0) { "srcLen must be >= 0" }

        var written = 0
        for (i in 0 until srcLen) {
            written += Character.toChars(src[srcOff + i], dest, destOff + written)
        }
        return written
    }

    abstract fun fill(buffer: KharacterBuffer, reader: Reader, numChars: Int = buffer.buffer.size): Boolean

    abstract fun offsetByCodePoints(buf: CharArray, start: Int, count: Int, index: Int, offset: Int): Int

    class Java5CharacterUtils: CharacterUtils() {

        override fun codePointAt(seq: CharSequence, offset: Int): Int {
            return Character.codePointAt(seq, offset)
        }

        override fun codePointAt(chars: CharArray, offset: Int, limit: Int): Int {
            return Character.codePointAt(chars, offset, limit)
        }

        override fun fill(buffer: KharacterBuffer, reader: Reader, numChars: Int): Boolean {
            assert(buffer.buffer.size >= 2) { "buffer size must be >= 2" }
            assert(numChars in 2..buffer.buffer.size) { "numCharrs must be 2 .. buffer size" }

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

    class Java4CharacterUtils: CharacterUtils() {

        override fun codePointAt(seq: CharSequence, offset: Int): Int {
            return seq[offset].code
        }

        override fun codePointAt(chars: CharArray, offset: Int, limit: Int): Int {
            assert(offset < limit) { "offset[$offset] must be less than limit[$limit]" }
            return chars[offset].code
        }

        override fun fill(buffer: KharacterBuffer, reader: Reader, numChars: Int): Boolean {
            assert(buffer.buffer.size >= 1)
            assert(numChars in 1..buffer.buffer.size) {
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

    class KharacterBuffer(val buffer: CharArray, var offset: Int, var length: Int) {

        var lastTrailingHighSurrogate: Char = 0.toChar()

        fun reset() {
            offset = 0
            length = 0
            lastTrailingHighSurrogate = 0.toChar()
        }
    }
}
