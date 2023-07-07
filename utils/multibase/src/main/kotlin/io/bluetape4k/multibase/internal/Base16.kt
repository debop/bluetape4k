package io.bluetape4k.multibase.internal

object Base16 {

    fun decode(hex: String): ByteArray {
        require(hex.length % 2 != 1) { "Must have an even number of hex digits to convert to bytes!" }

        val res = ByteArray(hex.length / 2)
        for (i in res.indices) res[i] = hex.substring(2 * i, 2 * i + 2).toInt(16).toByte()
        return res
    }

    fun encode(data: ByteArray): String {
        return bytesToHex(data)
    }

    private val HEX_DIGITS = arrayOf(
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"
    )
    private val HEX = Array<String>(256) {
        HEX_DIGITS[(it shr 4) and 0xF] + HEX_DIGITS[it and 0xF]
    }

    fun byteToHex(b: Byte): String? {
        return HEX[b.toInt() and 0xFF]
    }

    fun bytesToHex(data: ByteArray): String = buildString {
        data.forEach {
            append(byteToHex(it))
        }
    }
}
