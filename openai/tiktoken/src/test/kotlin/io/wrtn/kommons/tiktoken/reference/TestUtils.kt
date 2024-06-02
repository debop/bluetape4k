package io.bluetape4k.tiktoken.reference

import io.bluetape4k.logging.KLogging

object TestUtils: KLogging() {

    fun parseEncodingString(encodedString: String): List<Int> {
        return encodedString.substring(1, encodedString.length - 1)
            .replace("_", "")
            .split(",")
            .map { it.trim().toInt() }
    }
}
