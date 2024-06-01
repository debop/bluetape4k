package io.bluetape4k.multibase

import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.toUtf8Bytes
import io.bluetape4k.support.toUtf8String
import org.amshove.kluent.shouldBeEqualTo
import org.apache.commons.codec.binary.Hex
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class MultibaseTest {

    companion object: KLogging() {

        val faker = Fakers.faker

        private fun hexToBytes(s: String): ByteArray {
            return Hex.decodeHex(s)
        }

        private fun bytesToHex(bytes: ByteArray): String {
            return Hex.encodeHexString(bytes)
        }
    }

    fun getArguments(): List<Arguments> = listOf(
        Arguments.arguments(
            Multibase.Base.Base58BTC,
            hexToBytes("1220120F6AF601D46E10B2D2E11ED71C55D25F3042C22501E41D1246E7A1E9D3D8EC"),
            "zQmPZ9gcCEpqKTo6aq61g2nXGUhM4iCL3ewB6LDXZCtioEB"
        ),
        Arguments.arguments(
            Multibase.Base.Base58BTC,
            hexToBytes("1220BA8632EF1A07986B171B3C8FAF0F79B3EE01B6C30BBE15A13261AD6CB0D02E3A"),
            "zQmatmE9msSfkKxoffpHwNLNKgwZG8eT9Bud6YoPab52vpy"
        ),
        Arguments.arguments(
            Multibase.Base.Base58BTC,
            ByteArray(1),
            "z1"
        ),
        Arguments.arguments(
            Multibase.Base.Base58BTC,
            ByteArray(2),
            "z11"
        ),
        Arguments.arguments(
            Multibase.Base.Base58BTC,
            ByteArray(4),
            "z1111"
        ),
        Arguments.arguments(
            Multibase.Base.Base58BTC,
            ByteArray(8),
            "z11111111"
        ),
        Arguments.arguments(
            Multibase.Base.Base58BTC,
            ByteArray(16),
            "z1111111111111111"
        ),
        Arguments.arguments(
            Multibase.Base.Base58BTC,
            ByteArray(32),
            "z11111111111111111111111111111111"
        ),
        Arguments.arguments(
            Multibase.Base.Base16,
            hexToBytes("234ABED8DEBEDE"),
            "f234abed8debede"
        ),
        Arguments.arguments(
            Multibase.Base.Base16,
            hexToBytes("87AD873DEFC2B288"),
            "f87ad873defc2b288"
        ),
        Arguments.arguments(
            Multibase.Base.Base16,
            hexToBytes(""),
            "f"
        ),
        Arguments.arguments(
            Multibase.Base.Base16,
            hexToBytes("01"),
            "f01"
        ),
        Arguments.arguments(
            Multibase.Base.Base16,
            hexToBytes("0123456789ABCDEF"),
            "f0123456789abcdef"
        ),
        Arguments.arguments(
            Multibase.Base.Base16Upper,
            hexToBytes("446563656e7472616c697a652065766572797468696e67212121"),
            "F446563656E7472616C697A652065766572797468696E67212121"
        ),
        Arguments.arguments(
            Multibase.Base.Base32,
            hexToBytes("4D756C74696261736520697320617765736F6D6521205C6F2F"),
            "bjv2wy5djmjqxgzjanfzsaylxmvzw63lfeeqfy3zp"
        ),
        Arguments.arguments(
            Multibase.Base.Base32,
            hexToBytes("446563656e7472616c697a652065766572797468696e67212121"),
            "birswgzloorzgc3djpjssazlwmvzhs5dinfxgoijbee"
        ),
        Arguments.arguments(
            Multibase.Base.Base32,
            hexToBytes("01711220bb6ef01d25459cc803d0864cde4227cd2b779965eb1df34abeaec22c20fa42ea"),
            "bafyreif3n3yb2jkftteahuegjtpeej6nfn3zszpldxzuvpvoyiwcb6sc5i",
        ),
        Arguments.arguments(
            Multibase.Base.Base32,
            hexToBytes("0000000000"),
            "baaaaaaaa"
        ),
        Arguments.arguments(
            Multibase.Base.Base32,
            hexToBytes("446563656e7472616c697a652065766572797468696e67212121"),
            "birswgzloorzgc3djpjssazlwmvzhs5dinfxgoijbee"
        ),
        Arguments.arguments(
            Multibase.Base.Base32Pad,
            hexToBytes("446563656e7472616c697a652065766572797468696e67212121"),
            "cirswgzloorzgc3djpjssazlwmvzhs5dinfxgoijbee======"
        ),
        Arguments.arguments(
            Multibase.Base.Base32PadUpper,
            hexToBytes("446563656e7472616c697a652065766572797468696e67212121"),
            "CIRSWGZLOORZGC3DJPJSSAZLWMVZHS5DINFXGOIJBEE======"
        ),
        Arguments.arguments(
            Multibase.Base.Base32Upper,
            hexToBytes("446563656e7472616c697a652065766572797468696e67212121"),
            "BIRSWGZLOORZGC3DJPJSSAZLWMVZHS5DINFXGOIJBEE"
        ),
        Arguments.arguments(
            Multibase.Base.Base32Hex,
            hexToBytes("446563656e7472616c697a652065766572797468696e67212121"),
            "v8him6pbeehp62r39f9ii0pbmclp7it38d5n6e89144"
        ),
        Arguments.arguments(
            Multibase.Base.Base32HexPad,
            hexToBytes("446563656e7472616c697a652065766572797468696e67212121"),
            "t8him6pbeehp62r39f9ii0pbmclp7it38d5n6e89144======"
        ),
        Arguments.arguments(
            Multibase.Base.Base32HexPadUpper,
            hexToBytes("446563656e7472616c697a652065766572797468696e67212121"),
            "T8HIM6PBEEHP62R39F9II0PBMCLP7IT38D5N6E89144======"
        ),
        Arguments.arguments(
            Multibase.Base.Base32HexUpper,
            hexToBytes("446563656e7472616c697a652065766572797468696e67212121"),
            "V8HIM6PBEEHP62R39F9II0PBMCLP7IT38D5N6E89144"
        ),
        Arguments.arguments(
            Multibase.Base.Base36,
            hexToBytes("446563656e7472616c697a652065766572797468696e67212121"),
            "km552ng4dabi4neu1oo8l4i5mndwmpc3mkukwtxy9"
        ),
        Arguments.arguments(
            Multibase.Base.Base36,
            hexToBytes("00446563656e7472616c697a652065766572797468696e67212121"),
            "k0m552ng4dabi4neu1oo8l4i5mndwmpc3mkukwtxy9"
        ),
        Arguments.arguments(
            Multibase.Base.Base36Upper,
            hexToBytes("446563656e7472616c697a652065766572797468696e67212121"),
            "KM552NG4DABI4NEU1OO8L4I5MNDWMPC3MKUKWTXY9"
        ),
        Arguments.arguments(
            Multibase.Base.Base58BTC,
            hexToBytes("446563656e7472616c697a652065766572797468696e67212121"),
            "z36UQrhJq9fNDS7DiAHM9YXqDHMPfr4EMArvt"
        ),
        Arguments.arguments(
            Multibase.Base.Base64,
            hexToBytes("446563656e7472616c697a652065766572797468696e67212121"),
            "mRGVjZW50cmFsaXplIGV2ZXJ5dGhpbmchISE"
        ),
        Arguments.arguments(
            Multibase.Base.Base64Url,
            hexToBytes("446563656e7472616c697a652065766572797468696e67212121"),
            "uRGVjZW50cmFsaXplIGV2ZXJ5dGhpbmchISE"
        ),
        Arguments.arguments(
            Multibase.Base.Base64Pad,
            hexToBytes("446563656e7472616c697a652065766572797468696e67212121"),
            "MRGVjZW50cmFsaXplIGV2ZXJ5dGhpbmchISE="
        ),
        Arguments.arguments(
            Multibase.Base.Base64UrlPad,
            hexToBytes("446563656e7472616c697a652065766572797468696e67212121"),
            "URGVjZW50cmFsaXplIGV2ZXJ5dGhpbmchISE="
        ),
    )


    @ParameterizedTest(name = "{index}: {0}, {2}")
    @MethodSource("getArguments")
    fun `multibase encode`(base: Multibase.Base, raw: ByteArray, encoded: String) {
        val output = Multibase.encode(base, raw)
        output shouldBeEqualTo encoded

        val decoded = Multibase.decode(output)
        decoded shouldBeEqualTo raw
    }

    @ParameterizedTest(name = "{index}: {0}, {2}")
    @MethodSource("getArguments")
    fun `multibase decode`(base: Multibase.Base, raw: ByteArray, encoded: String) {
        val output = Multibase.encode(base, raw)
        output shouldBeEqualTo encoded

        val decoded = Multibase.decode(output)
        decoded shouldBeEqualTo raw
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("getArguments")
    fun `multibase encode decode`(base: Multibase.Base, raw: ByteArray, encoded: String) {
        val output = Multibase.encode(base, raw)
        output shouldBeEqualTo encoded

        val decoded = Multibase.decode(output)
        decoded shouldBeEqualTo raw
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("getArguments")
    fun `multibase with random string`(base: Multibase.Base) {
        val origin = Fakers.randomString(1024)

        val encoded = Multibase.encode(base, origin.toUtf8Bytes())
        log.debug { "base=$base, encoded=$encoded" }

        val decoded = Multibase.decode(encoded)
        val decodedString = decoded.toUtf8String()

        decodedString shouldBeEqualTo origin
    }
}
