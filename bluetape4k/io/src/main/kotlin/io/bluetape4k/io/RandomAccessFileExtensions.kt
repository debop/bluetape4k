package io.bluetape4k.io

import java.io.RandomAccessFile
import java.nio.ByteBuffer

/**
 * [RandomAccessFile] 정보를 읽어서 `dst`에 씁니다.
 * @receiver RandomAccessFile
 * @param dstBuffer ByteBuffer
 * @param limit Int
 * @return Int
 */
fun RandomAccessFile.putTo(dstBuffer: ByteBuffer, limit: Int = dstBuffer.remaining()): Int {
    return when {
        dstBuffer.hasArray() -> {
            val readCount = read(dstBuffer.array(), dstBuffer.arrayOffset() + dstBuffer.position(), limit)
            if (readCount > 0) {
                dstBuffer.position(dstBuffer.position() + readCount)
            }
            readCount
        }

        else -> {
            val array = ByteArray(limit)
            val readCount = read(array)

            if (readCount > 0) {
                dstBuffer.put(array)
            }
            readCount
        }
    }
}
