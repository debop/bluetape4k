package io.bluetape4k.http.hc5.entity.mime

import org.apache.hc.client5.http.entity.mime.HttpMultipartMode
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.HttpEntity
import org.apache.hc.core5.http.message.BasicNameValuePair
import java.nio.charset.Charset

inline fun multipartEntity(initializer: MultipartEntityBuilder.() -> Unit): HttpEntity {
    return MultipartEntityBuilder.create().apply(initializer).build()
}

inline fun multipartEntity(
    mode: HttpMultipartMode = HttpMultipartMode.STRICT,
    charset: Charset = Charsets.UTF_8,
    boundary: String? = null,
    subType: String? = null,
    contentType: ContentType? = null,
    parameters: Collection<BasicNameValuePair> = emptyList(),
    initializer: MultipartEntityBuilder.() -> Unit,
): HttpEntity {
    val builder = MultipartEntityBuilder.create()
        .setMode(mode)
        .setCharset(charset)

    boundary?.run { builder.setBoundary(boundary) }
    subType?.run { builder.setMimeSubtype(subType) }
    contentType?.run { builder.setContentType(contentType) }
    parameters.forEach {
        builder.addParameter(it)
    }

    return builder.apply(initializer).build()
}
