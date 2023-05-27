package io.bluetape4k.support

fun ByteArray.takeItems(count: Int): ByteArray =
    this.copyOfRange(0, count.coerceAtMost(size))

fun ByteArray.dropItems(count: Int): ByteArray =
    this.copyOfRange(count.coerceAtLeast(0), size)
