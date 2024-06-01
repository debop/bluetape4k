package io.bluetape4k.hyperscan.wrapper

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import java.util.*

object Utf8: KLogging() {

    fun byteToStringPositiveMap(str: String, bytesLength: Int): IntArray {
        log.trace { "byte to string position map. str=$str, bytesLength=$bytesLength" }
        val byteIndexes = IntArray(bytesLength)
        var currentByte = 0

        var stringPosition = 0
        while (stringPosition < str.length) {
            val c = str.codePointAt(stringPosition)
            val unicodeCharLength = when {
                c <= 0x7F     -> 1
                c <= 0x7FF    -> 2
                c <= 0xFFFF   -> 3
                c <= 0x1FFFFF -> 4
                else          -> throw IllegalArgumentException("Invalid unicode character: $c")
            }
            Arrays.fill(byteIndexes, currentByte, currentByte + unicodeCharLength, stringPosition)
            currentByte += unicodeCharLength

            if (Character.charCount(c) == 2) {
                stringPosition++
            }
            stringPosition++
        }

        log.trace { "byte to string position map. byteIndexes=${byteIndexes.contentToString()}" }
        return byteIndexes
    }
}
