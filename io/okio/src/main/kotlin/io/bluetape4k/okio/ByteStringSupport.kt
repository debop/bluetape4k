package io.bluetape4k.okio

import okio.ByteString

@JvmName("byteStringOfBytes")
fun byteStringOf(vararg bytes: Byte): ByteString = ByteString.of(*bytes)

@JvmName("byteStringOfByteArray")
fun byteStringOf(byteArray: ByteArray): ByteString = ByteString.of(*byteArray)