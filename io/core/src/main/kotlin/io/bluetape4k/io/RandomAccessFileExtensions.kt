package io.bluetape4k.io

import java.io.RandomAccessFile
import java.nio.ByteBuffer

/**
 * [RandomAccessFile] 정보를 읽어서 `dst`에 씁니다.
 * @receiver RandomAccessFile
 * @param dst ByteBuffer
 * @param limit Int
 * @return Int
 */
fun RandomAccessFile.putTo(dst: ByteBuffer, limit: Int = dst.remaining()): Int {
    return if (dst.hasArray()) {
        val readCount = read(dst.array(), dst.arrayOffset() + dst.position(), limit)
        if (readCount > 0) {
            dst.position(dst.position() + readCount)
        }
        readCount
    } else {
        val array = ByteArray(limit)
        val readCount = read(array)

        if (readCount > 0) {
            dst.put(array)
        }
        readCount
    }
}
