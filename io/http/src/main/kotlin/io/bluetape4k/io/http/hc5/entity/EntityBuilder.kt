package io.bluetape4k.io.http.hc5.entity

import io.bluetape4k.io.http.hc5.http.TEXT_PLAIN_UTF8
import io.bluetape4k.support.ifTrue
import org.apache.hc.client5.http.entity.EntityBuilder
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.HttpEntity
import org.apache.hc.core5.http.NameValuePair
import java.io.File
import java.io.InputStream
import java.io.Serializable

/**
 * Builder for [HttpEntity] instances.
 *
 * Several setter methods of this builder are mutually exclusive. In case of multiple invocations
 * of the following methods only the last one will have effect:
 *
 * - [setText(String)]
 * - [setBinary(ByteArray)]
 * - [setStream(java.io.InputStream)]
 * - [setSerializable(java.io.Serializable)]
 * - [setParameters(java.util.List)]
 * - [setParameters(NameValuePair...)]
 * - [setFile(java.io.File)]
 *
 * @param initializer  [EntityBuilder]를 이용한 초기화 코드
 * @receiver
 * @return [HttpEntity] 인스턴스
 */
inline fun httpEntity(initializer: EntityBuilder.() -> Unit): HttpEntity {
    return EntityBuilder.create().apply(initializer).build()
}

fun httpEntityOf(
    text: String? = null,
    contentType: ContentType = TEXT_PLAIN_UTF8,
    contentEncoding: String? = null,
    gzipCompressed: Boolean? = null,
): HttpEntity = httpEntity {
    setText(text)
    setContentType(contentType)
    contentEncoding?.run { setContentEncoding(this) }
    gzipCompressed?.ifTrue { gzipCompressed() }
}

fun httpEntityOf(
    binary: ByteArray,
    contentType: ContentType = ContentType.DEFAULT_BINARY,
    contentEncoding: String? = null,
    gzipCompressed: Boolean? = null,
): HttpEntity = httpEntity {
    setBinary(binary)
    setContentType(contentType)
    contentEncoding?.run { setContentEncoding(this) }
    gzipCompressed?.ifTrue { gzipCompressed() }
}

fun httpEntityOf(
    stream: InputStream,
    contentType: ContentType = ContentType.DEFAULT_BINARY,
    contentEncoding: String? = null,
    gzipCompressed: Boolean? = null,
): HttpEntity = httpEntity {
    setStream(stream)
    setContentType(contentType)
    contentEncoding?.run { setContentEncoding(this) }
    gzipCompressed?.ifTrue { gzipCompressed() }
}

fun httpEntityOf(
    file: File,
    contentType: ContentType = ContentType.DEFAULT_BINARY,
    contentEncoding: String? = null,
    gzipCompressed: Boolean? = null,
): HttpEntity = httpEntity {
    setFile(file)
    setContentType(contentType)
    contentEncoding?.run { setContentEncoding(this) }
    gzipCompressed?.ifTrue { gzipCompressed() }
}

fun httpEntityOf(
    serializable: Serializable,
    contentType: ContentType = ContentType.DEFAULT_BINARY,
    contentEncoding: String? = null,
    gzipCompressed: Boolean? = null,
): HttpEntity = httpEntity {
    setSerializable(serializable)
    setContentType(contentType)
    contentEncoding?.run { setContentEncoding(this) }
    gzipCompressed?.ifTrue { gzipCompressed() }
}

fun httpEntityOf(
    parameters: List<NameValuePair>,
    contentType: ContentType = TEXT_PLAIN_UTF8,
    contentEncoding: String? = null,
    gzipCompressed: Boolean? = null,
): HttpEntity = httpEntity {
    setParameters(parameters)
    setContentType(contentType)
    contentEncoding?.run { setContentEncoding(this) }
    gzipCompressed?.ifTrue { gzipCompressed() }
}
