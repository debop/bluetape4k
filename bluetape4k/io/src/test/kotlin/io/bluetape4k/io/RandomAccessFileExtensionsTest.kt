package io.bluetape4k.io

import io.bluetape4k.io.utils.Resourcex
import io.bluetape4k.logging.KLogging
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest

class RandomAccessFileExtensionsTest {

    companion object : KLogging() {
        private const val REPEAT_SIZE = 10
    }

    private val file = File("src/test/resources/files/Utf8Samples.txt")
    private val expected = Resourcex.getBytes("/files/Utf8Samples.txt")

    @RepeatedTest(REPEAT_SIZE)
    fun `file content put to byte buffer`() {
        val buffer = ByteBuffer.allocate(file.length().toInt())
        putToByteBuffer(buffer)
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `file content put to byte buffer direct`() {
        val buffer = ByteBuffer.allocateDirect(file.length().toInt())
        putToByteBuffer(buffer)
    }

    private fun putToByteBuffer(buffer: ByteBuffer) {
        RandomAccessFile(file, "r").use { raf ->
            val reads = raf.putTo(buffer)
            reads shouldBeEqualTo file.length().toInt()

            buffer.flip()
            buffer.getAllBytes() shouldBeEqualTo expected
        }
    }
}
