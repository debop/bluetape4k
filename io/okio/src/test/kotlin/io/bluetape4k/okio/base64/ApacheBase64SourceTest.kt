package io.bluetape4k.okio.base64

import io.bluetape4k.codec.encodeBase64String
import okio.Source

class ApacheBase64SourceTest: AbstractBaseNSourceTest() {

    override fun getSource(delegate: Source): Source {
        return ApacheBase64Source(delegate)
    }

    override fun getEncodedString(plainString: String): String {
        return plainString.encodeBase64String()
    }
}
