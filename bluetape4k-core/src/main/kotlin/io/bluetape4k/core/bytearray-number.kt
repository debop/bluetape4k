package io.bluetape4k.core

fun Int.toByteArray(): ByteArray = ByteArrays.intToByteArray(this)
fun Long.toByteArray(): ByteArray = ByteArrays.longToByteArray(this)

fun ByteArray.toInt(offset: Int = 0): Int = ByteArrays.byteArrayToInt(this, offset)
fun ByteArray.toLong(offset: Int = 0): Long = ByteArrays.byteArrayToLong(this, offset)
