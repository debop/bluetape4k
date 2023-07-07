package io.bluetape4k.multibase

import io.bluetape4k.core.requireNotEmpty
import io.bluetape4k.logging.KLogging
import io.bluetape4k.multibase.binary.Base32
import io.bluetape4k.multibase.binary.Base64
import io.bluetape4k.multibase.internal.Base16
import io.bluetape4k.multibase.internal.Base36
import io.bluetape4k.multibase.internal.Base58
import io.bluetape4k.support.toUtf8String
import java.util.*

object Multibase: KLogging() {


    enum class Base(var prefix: Char) {
        Base1('1'),
        Base2('0'),
        Base8('7'),
        Base10('9'),
        Base16('f'),
        Base16Upper('F'),
        Base32('b'),
        Base32Upper('B'),
        Base32Pad('c'),
        Base32PadUpper('C'),
        Base32Hex('v'),
        Base32HexUpper('V'),
        Base32HexPad('t'),
        Base32HexPadUpper('T'),
        Base36('k'),
        Base36Upper('K'),
        Base58BTC('z'),
        Base58Flickr('Z'),
        Base64('m'),
        Base64Url('u'),
        Base64Pad('M'),
        Base64UrlPad('U');

        companion object {
            private val lookup: MutableMap<Char, Base> = TreeMap()

            init {
                Base.values().forEach {
                    lookup[it.prefix] = it
                }
            }

            fun lookup(p: Char): Base {
                require(lookup.containsKey(p)) { "Unknown Multibase type: $p" }
                return lookup[p]!!
            }
        }
    }


    fun encode(b: Base, data: ByteArray): String {
        return when (b) {
            Base.Base58BTC -> b.prefix + Base58.encode(data)
            Base.Base16 -> b.prefix + Base16.encode(data)
            Base.Base16Upper -> b.prefix + Base16.encode(data).uppercase()
            Base.Base32 -> b.prefix.toString() + Base32().encode(data)?.toUtf8String()?.lowercase()
                ?.replace("=".toRegex(), "")

            Base.Base32Pad -> b.prefix.toString() + Base32().encode(data)?.toUtf8String()?.lowercase()
            Base.Base32PadUpper -> b.prefix.toString() + Base32().encode(data)?.toUtf8String()
            Base.Base32Upper -> b.prefix.toString() + Base32().encode(data)?.toUtf8String()?.replace("=".toRegex(), "")
            Base.Base32Hex -> b.prefix.toString() + Base32(useHex = true).encode(data)?.toUtf8String()
                ?.lowercase()?.replace("=".toRegex(), "")

            Base.Base32HexPad -> b.prefix.toString() + Base32(useHex = true).encode(data)?.toUtf8String()?.lowercase()
            Base.Base32HexPadUpper -> b.prefix.toString() + Base32(useHex = true).encode(data)?.toUtf8String()
            Base.Base32HexUpper -> b.prefix.toString() + Base32(useHex = true).encode(data)?.toUtf8String()
                ?.replace("=".toRegex(), "")

            Base.Base36 -> b.prefix + Base36.encode(data)
            Base.Base36Upper -> b.prefix + Base36.encode(data).uppercase()
            Base.Base64 -> b.prefix + Base64.encodeBase64String(data)!!.replace("=", "")
            Base.Base64Url -> b.prefix + Base64.encodeBase64URLSafeString(data)!!.replace("=", "")
            Base.Base64Pad -> b.prefix + Base64.encodeBase64String(data)!!
            Base.Base64UrlPad -> b.prefix + Base64.encodeBase64String(data)!!.replace("\\+", "-").replace("/", "_")
            else -> throw UnsupportedOperationException("Unsupported base encoding: " + b.name)
        }
    }

    fun encoding(data: String): Base {
        return Base.lookup(data[0])
    }

    fun decode(data: String): ByteArray? {
        data.requireNotEmpty("data")
        val b: Base = encoding(data)
        val rest = data.substring(1)

        return when (b) {
            Base.Base58BTC -> Base58.decode(rest)
            Base.Base16 -> Base16.decode(rest)
            Base.Base16Upper -> Base16.decode(rest.lowercase())
            Base.Base32, Base.Base32Pad -> Base32().decode(rest)
            Base.Base32PadUpper, Base.Base32Upper -> Base32().decode(rest.lowercase())
            Base.Base32Hex, Base.Base32HexPad -> Base32(useHex = true).decode(rest)
            Base.Base32HexPadUpper, Base.Base32HexUpper -> Base32(useHex = true).decode(rest.lowercase())
            Base.Base36 -> Base36.decode(rest)
            Base.Base36Upper -> Base36.decode(rest.lowercase())
            Base.Base64, Base.Base64Url, Base.Base64Pad, Base.Base64UrlPad -> Base64.decodeBase64(rest)

            else -> throw java.lang.UnsupportedOperationException("Unsupported base encoding: " + b.name)
        }
    }
}
