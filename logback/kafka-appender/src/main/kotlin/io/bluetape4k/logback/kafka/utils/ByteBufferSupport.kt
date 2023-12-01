package io.bluetape4k.logback.kafka.utils

import java.nio.ByteBuffer

fun String?.toHashBytes(): ByteArray? {
    return if (this.isNullOrBlank()) null
    else this.hashCode().toByteArray()
}

fun Int.toByteArray(): ByteArray =
    ByteBuffer.allocate(4).putInt(this).array()
