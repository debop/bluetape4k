package io.bluetape4k.okio

import io.bluetape4k.lang.reflectionToString
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.okio.coroutines.internal.SEGMENT_SIZE
import io.bluetape4k.okio.coroutines.internal.readAndWriteUnsafe
import io.bluetape4k.okio.coroutines.internal.readUnsafe
import okio.Buffer
import org.amshove.kluent.internal.assertFailsWith
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.*

class BufferCursorTest: AbstractOkioTest() {

    companion object: KLogging() {

        private val _factories = BufferFactory.entries.toList()
        private val _buffers = _factories.map { it.newBuffer() }
    }

    fun factories() = _factories
    fun buffers() = _buffers

    @Test
    fun `api example`() {
        val buffer = Buffer()
        buffer.readAndWriteUnsafe().use { cursor ->
            cursor.resizeBuffer(1_000_000)
            do {
                Arrays.fill(cursor.data!!, cursor.start, cursor.end, 'x'.code.toByte())
            } while (cursor.next() != -1)
            cursor.seek(3)
            cursor.data!![cursor.start] = 'o'.code.toByte()
            cursor.seek(1)
            cursor.data!![cursor.start] = 'o'.code.toByte()
            cursor.resizeBuffer(4)
        }
        log.debug { "buffer=$buffer" }
        buffer shouldBeEqualTo bufferOf("xoxo")
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `access segment by segment`(buffer: Buffer) {
        buffer.readUnsafe().use { cursor ->
            val actual = Buffer()
            while (cursor.next() != -1) {
                log.debug { "cursor.start=${cursor.start}, end=${cursor.end}" }
                actual.write(cursor.data!!, cursor.start, cursor.end - cursor.start)
            }
            actual shouldBeEqualTo buffer
        }
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `seek to negative one seeks before first segment`(buffer: Buffer) {
        buffer.readUnsafe().use { cursor ->
            cursor.seek(-1)
            cursor.offset shouldBeEqualTo -1
            cursor.data.shouldBeNull()
            cursor.start shouldBeEqualTo -1
            cursor.end shouldBeEqualTo -1

            cursor.next()
            cursor.offset shouldBeEqualTo 0
        }
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `access byte by byte`(buffer: Buffer) {
        buffer.readUnsafe().use { cursor ->
            val actual = ByteArray(buffer.size.toInt())
            repeat(buffer.size.toInt()) {
                cursor.seek(it.toLong())
                actual[it] = cursor.data!![cursor.start]
            }
            buffer.snapshot() shouldBeEqualTo byteStringOf(actual)
        }
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `access byte by byte reverse`(buffer: Buffer) {
        buffer.readUnsafe().use { cursor ->
            val actual = ByteArray(buffer.size.toInt())
            for (i in (buffer.size - 1).toInt() downTo 0) {
                cursor.seek(i.toLong())
                actual[i] = cursor.data!![cursor.start]
            }
            buffer.snapshot() shouldBeEqualTo byteStringOf(actual)
        }
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `access byte by byte always resetting to zero`(buffer: Buffer) {
        buffer.readUnsafe { cursor ->
            val actual = ByteArray(buffer.size.toInt())
            for (i in 0 until buffer.size) {
                cursor.seek(i)
                actual[i.toInt()] = cursor.data!![cursor.start]
                cursor.seek(0)  // 이렇게 하면 느려진다.
            }
            buffer.snapshot() shouldBeEqualTo byteStringOf(actual)
        }
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `segment by segment navigation`(buffer: Buffer) {
        buffer.readUnsafe { cursor ->
            cursor.offset shouldBeEqualTo -1L

            var lastOffset = cursor.offset
            while (cursor.next() != -1) {
                cursor.offset shouldBeGreaterThan lastOffset
                lastOffset = cursor.offset
            }
            cursor.offset shouldBeEqualTo buffer.size
            cursor.data.shouldBeNull()
            cursor.start shouldBeEqualTo -1
            cursor.end shouldBeEqualTo -1
        }
    }

    @ParameterizedTest
    @MethodSource("factories")
    fun `seek within segment`(factory: BufferFactory) {
        Assumptions.assumeTrue { factory == BufferFactory.SMALL_SEGMENTED_BUFFER }
        val buffer = factory.newBuffer()
        buffer.clone().readUtf8() shouldBeEqualTo "abcdefghijkl"  // abc//defg//hijkl (segment 마다 따로 저장되어 있다)

        buffer.readUnsafe { cursor ->
            cursor.seek(5L) shouldBeEqualTo 2  // 2 for 2 bytes left in the segment: "fg"
            cursor.offset shouldBeEqualTo 5L
            (cursor.end - cursor.start) shouldBeEqualTo 2

            Char(cursor.data!![cursor.start - 2].toUShort()).code shouldBeEqualTo 'd'.code
            Char(cursor.data!![cursor.start - 1].toUShort()).code shouldBeEqualTo 'e'.code
            Char(cursor.data!![cursor.start].toUShort()).code shouldBeEqualTo 'f'.code
            Char(cursor.data!![cursor.start + 1].toUShort()).code shouldBeEqualTo 'g'.code
        }
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `acquire and release`(buffer: Buffer) {
        val cursor = Buffer.UnsafeCursor()

        // Nothing initialized before acquire
        cursor.offset shouldBeEqualTo -1L
        cursor.data.shouldBeNull()
        cursor.start shouldBeEqualTo -1
        cursor.end shouldBeEqualTo -1

        buffer.readUnsafe(cursor)
        cursor.close()

        cursor.offset shouldBeEqualTo -1L
        cursor.data.shouldBeNull()
        cursor.start shouldBeEqualTo -1
        cursor.end shouldBeEqualTo -1
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `double acquire`(buffer: Buffer) {
        assertFailsWith<IllegalStateException> {
            buffer.readUnsafe { cursor ->
                buffer.readUnsafe(cursor)  // double acquire
            }
        }
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `release without acquire`(buffer: Buffer) {
        val cursor = Buffer.UnsafeCursor()
        assertFailsWith<IllegalStateException> {
            cursor.close()
        }
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `release after release`(buffer: Buffer) {
        val cursor = buffer.readUnsafe()
        cursor.close()

        assertFailsWith<IllegalStateException> {
            cursor.close()
        }
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `enlarge within segment`(buffer: Buffer) {
        val originalSize = buffer.size
        val expected = Buffer()
        buffer.clone().copyTo(expected)
        expected.writeUtf8("abc")

        log.debug { "buffer=${buffer.reflectionToString()}" }
        buffer.readAndWriteUnsafe { cursor ->
            cursor.resizeBuffer(originalSize + 3) shouldBeEqualTo originalSize

            cursor.seek(originalSize)
            cursor.data!![cursor.start] = 'a'.code.toByte()

            cursor.seek(originalSize + 1)
            cursor.data!![cursor.start] = 'b'.code.toByte()

            cursor.seek(originalSize + 2)
            cursor.data!![cursor.start] = 'c'.code.toByte()
        }
        buffer shouldBeEqualTo expected
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `enlarge by many segment`(buffer: Buffer) {
        val originalSize = buffer.size
        val expected = Buffer()
        buffer.clone().copyTo(expected)
        expected.writeUtf8("x".repeat(1_000_000))

        buffer.readAndWriteUnsafe { cursor ->
            cursor.resizeBuffer(originalSize + 1_000_000)
            cursor.seek(originalSize)

            do {
                Arrays.fill(cursor.data!!, cursor.start, cursor.end, 'x'.code.toByte())
            } while (cursor.next() != -1)
        }
        buffer shouldBeEqualTo expected
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `resize not acquired`(buffer: Buffer) {
        val cursor = Buffer.UnsafeCursor()
        assertFailsWith<IllegalStateException> {
            cursor.resizeBuffer(10)
        }
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `expand not acquired`(buffer: Buffer) {
        val cursor = Buffer.UnsafeCursor()
        assertFailsWith<IllegalStateException> {
            cursor.expandBuffer(10)
        }
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `resize acquire readonly`(buffer: Buffer) {
        assertFailsWith<IllegalStateException> {
            buffer.readUnsafe { cursor ->
                cursor.resizeBuffer(10)
            }
        }
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `expand acquired readonly`(buffer: Buffer) {
        assertFailsWith<IllegalStateException> {
            buffer.readUnsafe { cursor ->
                cursor.expandBuffer(10)
            }
        }
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun shrink(buffer: Buffer) {
        Assumptions.assumeTrue { buffer.size > 3 }
        val originalSize = buffer.size
        val expected = Buffer()
        buffer.clone().copyTo(expected, 0, originalSize - 3)
        buffer.readAndWriteUnsafe { cursor ->
            cursor.resizeBuffer(originalSize - 3) shouldBeEqualTo originalSize
        }
        buffer shouldBeEqualTo expected
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `shink by many segments`(buffer: Buffer) {
        Assumptions.assumeTrue { buffer.size <= 1_000_000 }

        val originalSize = buffer.size
        val toShrink = bufferOf("x".repeat(1_000_000))
        buffer.clone().copyTo(toShrink, 0, originalSize)

        val unsafeCursor = Buffer.UnsafeCursor()
        toShrink.readAndWriteUnsafe(unsafeCursor) { cursor ->
            cursor.resizeBuffer(originalSize)
            log.debug { "cursor=${cursor.reflectionToString()}" }
        }
        val expected = bufferOf("x".repeat(originalSize.toInt()))
        toShrink shouldBeEqualTo expected
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `shrink adjust offset`(buffer: Buffer) {
        Assumptions.assumeTrue { buffer.size > 4 }

        buffer.readAndWriteUnsafe { cursor ->
            cursor.seek(buffer.size - 1)
            cursor.resizeBuffer(3)
            log.debug { "cursor=${cursor.reflectionToString()}" }

            cursor.offset shouldBeEqualTo 3L
            cursor.data.shouldBeNull()
            cursor.start shouldBeEqualTo -1
            cursor.end shouldBeEqualTo -1
        }
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `resize to same size seeks to end`(buffer: Buffer) {
        val originalSize = buffer.size

        buffer.readAndWriteUnsafe { cursor ->
            cursor.seek(buffer.size / 2)
            buffer.size shouldBeEqualTo originalSize

            cursor.resizeBuffer(originalSize)
            log.debug { "cursor=${cursor.reflectionToString()}" }

            buffer.size shouldBeEqualTo originalSize
            cursor.offset shouldBeEqualTo originalSize

            cursor.data.shouldBeNull()
            cursor.start shouldBeEqualTo -1
            cursor.end shouldBeEqualTo -1
        }
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `resize enlarge moves cursor to old size`(buffer: Buffer) {
        val originalSize = buffer.size
        val expected = Buffer()
        buffer.clone().copyTo(expected)
        expected.writeUtf8("a")

        buffer.readAndWriteUnsafe { cursor ->
            cursor.seek(buffer.size / 2)
            buffer.size shouldBeEqualTo originalSize

            cursor.resizeBuffer(originalSize + 1)
            log.debug { "cursor=${cursor.reflectionToString()}" }
            cursor.offset shouldBeEqualTo originalSize

            cursor.data.shouldNotBeNull()
            cursor.start shouldNotBeEqualTo -1
            cursor.end shouldBeEqualTo (cursor.start + 1)
            cursor.data!![cursor.start] = 'a'.code.toByte()
        }
        buffer shouldBeEqualTo expected
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `resize shrink moves cursor to end`(buffer: Buffer) {
        Assumptions.assumeTrue { buffer.size > 0L }

        val originalSize = buffer.size

        log.debug { "buffer=${buffer.reflectionToString()}" }
        buffer.readAndWriteUnsafe { cursor ->
            cursor.seek(buffer.size / 2)
            buffer.size shouldBeEqualTo originalSize

            cursor.resizeBuffer(originalSize - 1)
            log.debug { "cursor=${cursor.reflectionToString()}" }
            cursor.offset shouldBeEqualTo originalSize - 1

            cursor.data.shouldBeNull()
            cursor.start shouldBeEqualTo -1
            cursor.end shouldBeEqualTo -1
        }
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun expand(buffer: Buffer) {
        val originalSize = buffer.size
        val expected = bufferOf(buffer)
        expected.writeUtf8("abcde")

        buffer.readAndWriteUnsafe().use { cursor ->
            cursor.expandBuffer(5)
            for (i in 0..4) {
                cursor.data!![cursor.start + i] = ('a'.code + i).toByte()
            }
            cursor.resizeBuffer(originalSize + 5)
        }
        buffer shouldBeEqualTo expected
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `expand same segment`(buffer: Buffer) {
        Assumptions.assumeTrue { buffer.size > 0 }

        val originalSize = buffer.size

        buffer.readAndWriteUnsafe().use { cursor ->
            cursor.seek(originalSize - 1)
            val originalEnd = cursor.end
            log.debug { "original end=$originalEnd" }
            Assumptions.assumeTrue { originalEnd < SEGMENT_SIZE.toInt() }

            val addedByteCount = cursor.expandBuffer(1)
            addedByteCount shouldBeEqualTo SEGMENT_SIZE - originalEnd
            buffer.size shouldBeEqualTo originalSize + addedByteCount
            cursor.offset shouldBeEqualTo originalSize
            cursor.start shouldBeEqualTo originalEnd
            cursor.end shouldBeEqualTo SEGMENT_SIZE.toInt()
        }
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `expand new segment`(buffer: Buffer) {
        val originalSize = buffer.size

        buffer.readAndWriteUnsafe { cursor ->
            val addedByteCount = cursor.expandBuffer(SEGMENT_SIZE.toInt())
            addedByteCount shouldBeEqualTo SEGMENT_SIZE
            cursor.offset = originalSize
            cursor.start shouldBeEqualTo 0
            cursor.end shouldBeEqualTo SEGMENT_SIZE.toInt()
        }
    }

    @ParameterizedTest
    @MethodSource("buffers")
    fun `expand moves offset to old size`(buffer: Buffer) {
        val originalSize = buffer.size

        buffer.readAndWriteUnsafe { cursor ->
            cursor.seek(buffer.size / 2)
            buffer.size shouldBeEqualTo originalSize

            val addedByteCount = cursor.expandBuffer(5)
            buffer.size shouldBeEqualTo originalSize + addedByteCount
            cursor.offset shouldBeEqualTo originalSize
        }
    }
}
