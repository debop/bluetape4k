package io.bluetape4k.io

import java.io.InputStream
import java.io.PushbackInputStream
import java.nio.charset.Charset
import kotlin.text.Charsets.UTF_8

private const val BOM_SIZE = 4
private const val ZZ = 0x00.toByte()
private const val EF = 0xEF.toByte()
private const val BB = 0xBB.toByte()
private const val BF = 0xBF.toByte()
private const val FE = 0xFE.toByte()
private const val FF = 0xFF.toByte()

fun ByteArray.getBOM(defaultCharset: Charset = UTF_8): Pair<Int, Charset> {
    val bom = this.copyOf(4)

    when (bom) {
        byteArrayOf(ZZ, ZZ, FE, FF) -> return Pair(4, Charsets.UTF_32BE)
        byteArrayOf(FE, ZZ, ZZ, ZZ) -> return Pair(4, Charsets.UTF_32LE)
    }
    when (bom.copyOf(3)) {
        byteArrayOf(EF, BB, BF) -> return Pair(3, UTF_8)
    }
    when (bom.copyOf(2)) {
        byteArrayOf(FE, FF) -> return Pair(2, Charsets.UTF_16BE)
        byteArrayOf(FF, FE) -> return Pair(2, Charsets.UTF_16LE)
    }
    return Pair(0, defaultCharset)
}

fun ByteArray.removeBom(defaultCharset: Charset = UTF_8): Pair<ByteArray, Charset> {
    val (skipSize, charset) = getBOM(defaultCharset)
    val array = if (skipSize > 0) this.copyOfRange(skipSize, size) else this

    return array to charset
}

fun InputStream.withoutBom(defaultCharset: Charset = UTF_8): Pair<InputStream, Charset> {
    val bom = ByteArray(BOM_SIZE)
    val pushbackStream = PushbackInputStream(this, BOM_SIZE)
    val readSize = pushbackStream.read(bom, 0, bom.size)
    val (skipSize, charset) = bom.getBOM(defaultCharset)

    pushbackStream.unread(bom, skipSize, readSize - skipSize)
    return pushbackStream to charset
}
