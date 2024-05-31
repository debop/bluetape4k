package io.bluetape4k.junit5.output

import io.bluetape4k.logging.KLogging
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.PrintStream

/**
 * 테스트 시 [System.out], [System.err]로 출력되는 정보를 메모리에 저장했다가 `capture` 메소드를 통해 제공합니다.
 */
class OutputCapturer {

    companion object: KLogging()

    private var copy: ByteArrayOutputStream? = null
    private var captureOut: CaptureOutputStream? = null
    private var captureErr: CaptureOutputStream? = null

    inline fun expect(body: (String) -> Unit) {
        body(capture())
    }

    fun capture(): String {
        flush()
        return copy?.toString(Charsets.UTF_8) ?: ""
    }

    fun flush() {
        captureOut?.run { flush() }
        captureErr?.run { flush() }
    }

    override fun toString(): String {
        return capture()
    }

    internal fun startCapture() {
        copy = ByteArrayOutputStream()
        captureOut = CaptureOutputStream(System.out, copy!!)
        captureErr = CaptureOutputStream(System.err, copy!!)

        System.setOut(PrintStream(captureOut!!))
        System.setErr(PrintStream(captureErr!!))
    }

    internal fun finishCapture() {
        System.setOut(captureOut?.origin)
        System.setErr(captureErr?.origin)

        copy?.close()
        captureOut?.close()
        captureErr?.close()
    }

    private class CaptureOutputStream(val origin: PrintStream, val copy: OutputStream): OutputStream() {

        override fun write(b: Int) {
            copy.write(b)
            origin.write(b)
            origin.flush()
        }

        override fun write(b: ByteArray) {
            write(b, 0, b.size)
        }

        override fun write(b: ByteArray, off: Int, len: Int) {
            copy.write(b, off, len)
            origin.write(b, off, len)
            origin.flush()
        }

        override fun flush() {
            origin.flush()
            copy.flush()
        }

        override fun close() {
            origin.close()
            copy.close()
        }

    }
}
