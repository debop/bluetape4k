package io.bluetape4k.io

import java.io.RandomAccessFile
import java.nio.ByteBuffer

/**
 * [RandomAccessFile] 정보를 읽어서 [ByteBuffer]에 씁니다.
 *
 * @receiver RandomAccessFile  읽을 파일
 * @param dstBuffer ByteBuffer 쓸 버퍼
 * @param limit Int 쓸 크기
 * @return Int 읽은 크기
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

        else                 -> {
            val array = ByteArray(limit)
            val readCount = read(array)

            if (readCount > 0) {
                dstBuffer.put(array)
            }
            readCount
        }
    }
}
