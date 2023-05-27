package io.bluetape4k.codec

import io.bluetape4k.logging.KLogging

object AsciiRadixCoders: KLogging() {

    private const val TABLE36: String = "0123456789abcdefghijklmnopqrstuvwxyz"
    private const val TABLE58: String = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"

    val ASCII36 by lazy {
        (2..TABLE36.length).map { AsciiRadixCoder(TABLE36.substring(0, it)) }
    }

    val ASCII58 by lazy {
        (2..TABLE58.length).map { AsciiRadixCoder(TABLE58.substring(0, it)) }
    }

    fun withBase(base: Int): AsciiRadixCoder {
        val bytes = ByteArray(base) { it.toByte() }
        return AsciiRadixCoder(String(bytes, Charsets.US_ASCII))
    }

    val ALL: List<AsciiRadixCoder> by lazy {
        (2..128).map { withBase(it) }
    }
}
