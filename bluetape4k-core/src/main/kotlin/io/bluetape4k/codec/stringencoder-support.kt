package io.bluetape4k.codec

import io.bluetape4k.core.support.EMPTY_STRING
import io.bluetape4k.core.support.emptyByteArray


private val base64Encoder = Base64StringEncoder()
private val hexEncoder = HexStringEncoder()


private fun String.toUtf8Bytes(): ByteArray = toByteArray(Charsets.UTF_8)
private fun ByteArray.toUtf8String(): String = toString(Charsets.UTF_8)

fun ByteArray?.encodeBase64ByteArray(): ByteArray =
    this?.run { base64Encoder.encode(this).toUtf8Bytes() } ?: emptyByteArray

fun ByteArray?.encodeBase64String(): String =
    this?.run { base64Encoder.encode(this) } ?: EMPTY_STRING

fun String?.encodeBase64ByteArray(): ByteArray =
    this?.run { base64Encoder.encode(this.toUtf8Bytes()).toUtf8Bytes() } ?: emptyByteArray

fun String?.encodeBase64String(): String =
    this?.run { base64Encoder.encode(this.toUtf8Bytes()) } ?: EMPTY_STRING


fun ByteArray?.decodeBase64ByteArray(): ByteArray =
    this?.run { base64Encoder.decode(this.toUtf8String()) } ?: emptyByteArray

fun ByteArray?.decodeBase64String(): String =
    this?.run { base64Encoder.decode(this.toUtf8String()).toUtf8String() } ?: EMPTY_STRING

fun String?.decodeBase64ByteArray(): ByteArray =
    this?.run { base64Encoder.decode(this) } ?: emptyByteArray

fun String?.decodeBase64String(): String =
    this?.run { base64Encoder.decode(this).toUtf8String() } ?: EMPTY_STRING


fun ByteArray?.encodeHexByteArray(): ByteArray =
    this?.run { hexEncoder.encode(this).toUtf8Bytes() } ?: emptyByteArray

fun ByteArray?.encodeHexString(): String =
    this?.run { hexEncoder.encode(this) } ?: EMPTY_STRING

fun String?.encodeHexByteArray(): ByteArray =
    this?.run { hexEncoder.encode(this.toUtf8Bytes()).toUtf8Bytes() } ?: emptyByteArray

fun String?.encodeHexString(): String =
    this?.run { hexEncoder.encode(this.toUtf8Bytes()) } ?: EMPTY_STRING


fun ByteArray?.decodeHexByteArray(): ByteArray =
    this?.run { hexEncoder.decode(this.toUtf8String()) } ?: emptyByteArray

fun ByteArray?.decodeHexString(): String =
    this?.run { hexEncoder.decode(this.toUtf8String()).toUtf8String() } ?: EMPTY_STRING

fun String?.decodeHexByteArray(): ByteArray =
    this?.run { hexEncoder.decode(this) } ?: emptyByteArray

fun String?.decodeHexString(): String =
    this?.run { hexEncoder.decode(this).toUtf8String() } ?: EMPTY_STRING
