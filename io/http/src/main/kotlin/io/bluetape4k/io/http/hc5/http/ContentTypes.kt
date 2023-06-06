package io.bluetape4k.io.http.hc5.http

import org.apache.hc.core5.http.ContentType

object ContentTypes {

    @JvmField
    val TEXT_PLAIN_UTF8: ContentType = ContentType.create("text/plain", Charsets.UTF_8)

}
