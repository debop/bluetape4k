package io.bluetape4k.io

import java.net.URL
import java.net.URLConnection

/**
 * URL 정보를 ByteArray로 읽어옵니다.
 *
 * @receiver URL 읽을 URL
 * @return ByteArray 읽은 바이트 배열
 */
fun URL.toByteArray(): ByteArray = openStream().use { it.toByteArray() }

/**
 * URLConnection 정보를 ByteArray로 읽어옵니다.
 *
 * @receiver URLConnection 읽을 URLConnection
 * @return ByteArray 읽은 바이트 배열
 */
fun URLConnection.toByteArray(): ByteArray = getInputStream().use { it.toByteArray() }
