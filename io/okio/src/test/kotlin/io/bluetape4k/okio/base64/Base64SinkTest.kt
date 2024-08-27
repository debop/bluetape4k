package io.bluetape4k.okio.base64

import io.bluetape4k.logging.KLogging
import io.bluetape4k.okio.byteStringOf
import okio.Sink

class Base64SinkTest: AbstractBaseNSinkTest() {

    companion object: KLogging()

    override fun createSink(delegate: Sink): Sink = Base64Sink(delegate)

    override fun getEncodedString(expectedBytes: ByteArray): String {
        return byteStringOf(expectedBytes).base64()
    }
}
