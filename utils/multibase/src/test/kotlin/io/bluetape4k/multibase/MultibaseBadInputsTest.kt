package io.bluetape4k.multibase

import io.bluetape4k.logging.KLogging
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertFailsWith

class MultibaseBadInputsTest {

    companion object: KLogging()

    private fun getArguments(): List<String> = listOf(
        "f012", // Hex string of odd length, not allowed in Base16
        "f0g", // 'g' char is not allowed in Base16
        "zt1Zv2yaI", // 'I' char is not allowed in Base58
        "2", // '2' is not a valid encoding marker
        "" // Empty string is not a valid multibase
    )

    @ParameterizedTest(name = """{index}: "{0}"""")
    @MethodSource("getArguments")
    fun `bad inputs`(input: String) {
        assertFailsWith<IllegalArgumentException> {
            Multibase.decode(input)
        }
    }
}
