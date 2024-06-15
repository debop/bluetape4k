package io.bluetape4k.okio.tests

import okio.Buffer
import kotlin.random.Random

object TestUtil {

    const val SEGMENT_POOL_MAX_SIZE = 64 * 1024
    const val SEGMENT_SIZE = 8192

    const val REPLACEMENT_BYTE: Byte = '?'.code.toByte()
    const val REPLACEMENT_CHARACTER: Char = '\ufffd'
    const val REPLACEMENT_CODE_POINT: Int = REPLACEMENT_CHARACTER.code

    /**
     * Returns a new buffer containing the contents of `segments`, attempting to isolate each
     * string to its own segment in the returned buffer. This clones buffers so that segments are
     * shared, preventing compaction from occurring.
     */
    fun bufferWithSegments(vararg segments: String): Buffer {
        val result = Buffer()
        for (segment in segments) {
            val offsetInSegment = if (segment.length < SEGMENT_SIZE) (SEGMENT_SIZE - segment.length) / 2 else 0
            val buffer = Buffer().apply {
                writeUtf8("_".repeat(offsetInSegment))
                writeUtf8(segment)
                skip(offsetInSegment.toLong())
            }
            result.write(buffer.copy(), buffer.size)
        }
        return result
    }

    fun bufferWithRandomSegmentLayout(random: Random, data: ByteArray): Buffer {
        val result = Buffer()

        // Writing to result directly will yield packed segments. Instead, write to
        // other buffers, then write those buffers to result.
        var pos = 0
        var byteCount: Int
        while (pos < data.size) {
            byteCount = SEGMENT_SIZE / 2 + random.nextInt(SEGMENT_SIZE / 2)
            if (byteCount > data.size - pos) byteCount = data.size - pos
            val offset = random.nextInt(SEGMENT_SIZE - byteCount)

            val segment = Buffer().apply {
                write(ByteArray(offset))
                write(data, pos, byteCount)
                skip(offset.toLong())
            }

            result.write(segment, byteCount.toLong())
            pos += byteCount
        }

        return result
    }


}
