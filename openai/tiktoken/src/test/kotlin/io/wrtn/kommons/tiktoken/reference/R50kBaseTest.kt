package io.bluetape4k.tiktoken.reference

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import io.bluetape4k.tiktoken.Encodings
import io.bluetape4k.tiktoken.api.EncodingType
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource

class R50kBaseTest {

    companion object: KLogging() {
        private val ENCODING = Encodings.newDefaultEncodingRegistry().getEncoding(EncodingType.R50K_BASE)
        private const val CSV_FILE = "/encidings/r50k_base_encodings.csv"
        private const val MAX_CHARS = 1_000_000
    }

    @ParameterizedTest
    @CsvFileSource(resources = [CSV_FILE], numLinesToSkip = 1, maxCharsPerColumn = MAX_CHARS)
    fun `encodes correctly`(input: String, output: String) {
        log.trace { "input=$input, output=$output" }

        val expected = TestUtils.parseEncodingString(output)
        val actual = ENCODING.encode(input)
        actual shouldBeEqualTo expected
    }

    @ParameterizedTest
    @CsvFileSource(resources = [CSV_FILE], numLinesToSkip = 1, maxCharsPerColumn = MAX_CHARS)
    fun `encodes stable`(input: String) {
        val actual = ENCODING.decode(ENCODING.encode(input))
        actual shouldBeEqualTo input
    }

    @ParameterizedTest
    @CsvFileSource(resources = [CSV_FILE], numLinesToSkip = 1, maxCharsPerColumn = MAX_CHARS)
    fun `encodes correctly with max tokens set`(input: String, output: String, outputMaxTokens10: String) {
        val expected = TestUtils.parseEncodingString(output)
        val expectedWithMaxTokens = TestUtils.parseEncodingString(outputMaxTokens10)

        val encodingResult = ENCODING.encode(input, 10)

        encodingResult.tokens shouldBeEqualTo expectedWithMaxTokens
        encodingResult.truncated shouldBeEqualTo (expected.size > expectedWithMaxTokens.size)
    }

    @ParameterizedTest
    @CsvFileSource(resources = [CSV_FILE], numLinesToSkip = 1, maxCharsPerColumn = MAX_CHARS)
    fun `encodes stable with max tokens set`(input: String) {
        val actual = ENCODING.decode(ENCODING.encode(input, 10).tokens)
        input.startsWith(actual).shouldBeTrue()
    }

    @ParameterizedTest
    @CsvFileSource(resources = [CSV_FILE], numLinesToSkip = 1, maxCharsPerColumn = MAX_CHARS)
    fun `encode ordinary encodes correctly`(input: String, output: String) {
        val expected = TestUtils.parseEncodingString(output)
        val actual = ENCODING.encodeOrdinary(input)

        actual shouldBeEqualTo expected
    }


    @ParameterizedTest
    @CsvFileSource(resources = [CSV_FILE], numLinesToSkip = 1, maxCharsPerColumn = MAX_CHARS)
    fun `encode ordinary encodes correctly max tokens`(input: String, output: String, outputMaxTokens10: String) {
        val expected = TestUtils.parseEncodingString(output)
        val expectedWithMaxTokens = TestUtils.parseEncodingString(outputMaxTokens10)

        val actual = ENCODING.encodeOrdinary(input, 10)

        actual.tokens shouldBeEqualTo expectedWithMaxTokens
        actual.truncated shouldBeEqualTo (expected.size > expectedWithMaxTokens.size)
    }

    @ParameterizedTest
    @CsvFileSource(resources = [CSV_FILE], numLinesToSkip = 1, maxCharsPerColumn = MAX_CHARS)
    fun `encode ordinary encodes stable`(input: String) {
        val actual = ENCODING.decode(ENCODING.encodeOrdinary(input))
        actual shouldBeEqualTo input
    }

    @ParameterizedTest
    @CsvFileSource(resources = [CSV_FILE], numLinesToSkip = 1, maxCharsPerColumn = MAX_CHARS)
    fun `encode ordinary encodes stable with max tokens set`(input: String) {
        val actual = ENCODING.decode(ENCODING.encodeOrdinary(input, 10).tokens)

        input.startsWith(actual).shouldBeTrue()
    }

    @Test
    fun `encode ordinary encodes special tokens correctly`() {
        val input = "Hello<|endoftext|>, <|fim_prefix|> <|fim_middle|> world <|fim_suffix|> ! <|endofprompt|>"
        val actual = ENCODING.decode(ENCODING.encodeOrdinary(input))

        actual shouldBeEqualTo input
    }

    @Test
    fun `encode ordinary with punctuation`() {
        val input = "How about some punctuation?!!"
        val tokens = listOf(2437, 546, 617, 21025, 2288, 30, 3228)

        val actual = ENCODING.encodeOrdinary(input)
        actual shouldBeEqualTo tokens

    }
}