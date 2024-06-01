package io.bluetape4k.http.hc5.entity

import org.apache.hc.core5.http.HttpEntity
import org.apache.hc.core5.http.NameValuePair
import org.apache.hc.core5.http.io.entity.EntityUtils

/**
 * Ensures that the entity content is fully consumed and the content stream, if exists,
 * is closed. The process is done, ***quietly*** , without throwing any IOException.
 */
fun HttpEntity?.consumeQuietly() {
    this?.run { EntityUtils.consumeQuietly(this) }
}

/**
 * Ensures that the entity content is fully consumed and the content stream, if exists, is closed.
 */
fun HttpEntity?.consume() {
    this?.run { EntityUtils.consume(this) }
}

/**
 * Reads the contents of an entity and return it as a byte array.
 *
 * @param maxResultLength The maximum size of the String to return; use it to guard against unreasonable or malicious processing.
 * @return  byte array containing the entity content. May be null if [HttpEntity.getContent] is null.
 */
fun HttpEntity.toByteArrayOrNull(maxResultLength: Int = Int.MAX_VALUE): ByteArray? {
    return EntityUtils.toByteArray(this, maxResultLength)
}

/**
 * Gets the entity content as a String, using the provided default character set
 * if none is found in the entity.
 * If defaultCharset is null, the default "UTF-8" is used.
 *
 * @param cs charset
 * @param maxResultLength The maximum size of the String to return; use it to guard against unreasonable or malicious processing.
 * @return the entity content as a String. May be null if [HttpEntity.getContent] is null.
 */
fun HttpEntity.toStringOrNull(
    maxResultLength: Int = Int.MAX_VALUE,
): String? {
    return EntityUtils.toString(this, maxResultLength)
}

/**
 * Returns a list of [NameValuePair] as parsed from an [HttpEntity].
 * The encoding is taken from the entity's Content-Encoding header.
 *
 * @param maxResultLength  The maximum size of the stream to read; use it to guard against unreasonable or malicious processing.
 * @return  a list of [NameValuePair] as built from the URI's query portion.
 */
fun HttpEntity.parse(maxResultLength: Int = Int.MAX_VALUE): List<NameValuePair> =
    EntityUtils.parse(this, maxResultLength)
