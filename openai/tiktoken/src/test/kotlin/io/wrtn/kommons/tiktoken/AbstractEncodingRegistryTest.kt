package io.bluetape4k.tiktoken

import io.bluetape4k.support.emptyByteArray
import io.bluetape4k.tiktoken.api.Encoding
import io.bluetape4k.tiktoken.api.EncodingResult
import io.bluetape4k.tiktoken.api.EncodingType
import io.bluetape4k.tiktoken.api.ModelType
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

abstract class AbstractEncodingRegistryTest<T: AbstractEncodingRegistry> {

    protected abstract val registry: T
    protected abstract val initializer: (T) -> Unit

    @BeforeEach
    fun beforeEach() {
        initializer(registry)
    }

    @ParameterizedTest(name = "getEncodingByName {0}")
    @EnumSource(EncodingType::class)
    fun `get encoding`(encodingType: EncodingType) {
        val encoding = registry.getEncoding(encodingType)
        encoding.shouldNotBeNull()
        encoding.name shouldBeEqualTo encodingType.encodingName
    }

    @ParameterizedTest(name = "getEncodingByName {0}")
    @EnumSource(EncodingType::class)
    fun `get encoding by name`(encodingType: EncodingType) {
        val encoding = registry.getEncoding(encodingType.encodingName)
        encoding.shouldNotBeNull()
        encoding.name shouldBeEqualTo encodingType.encodingName
    }

    @ParameterizedTest
    @EnumSource(ModelType::class)
    fun `get encoding for model`(modelType: ModelType) {
        val encoding = registry.getEncodingForModel(modelType)
        encoding.shouldNotBeNull()
        encoding.name shouldBeEqualTo modelType.encodingType.encodingName
    }

    @ParameterizedTest
    @EnumSource(ModelType::class)
    fun `get encoding for model by name`(modelType: ModelType) {
        val encoding = registry.getEncodingForModel(modelType.modelName)
        encoding.shouldNotBeNull()
        encoding.name shouldBeEqualTo modelType.encodingType.encodingName
    }

    @Test
    fun `get encoding for model by prefix - GPT-4-32k-0314`() {
        val encoding = registry.getEncodingForModel("gpt-4-32k-0314")
        encoding.shouldNotBeNull()
        encoding.name shouldBeEqualTo ModelType.GPT_4.encodingType.encodingName
    }

    @Test
    fun `get encoding for model by prefix - GPT-4-0314`() {
        val encoding = registry.getEncodingForModel("gpt-4-0314")
        encoding.shouldNotBeNull()
        encoding.name shouldBeEqualTo ModelType.GPT_4.encodingType.encodingName
    }

    @Test
    fun `get encoding for model by prefix - GPT-3_5-turbo-0301`() {
        val encoding = registry.getEncodingForModel("gpt-3.5-turbo-0301")
        encoding.shouldNotBeNull()
        encoding.name shouldBeEqualTo ModelType.GPT_3_5_TURBO.encodingType.encodingName
    }

    @Test
    fun `get encoding for model by prefix - GPT-3_5-turbo-16k-0613`() {
        val encoding = registry.getEncodingForModel("gpt-3.5-turbo-16k-0613")
        encoding.shouldNotBeNull()
        encoding.name shouldBeEqualTo ModelType.GPT_3_5_TURBO_16K.encodingType.encodingName
    }

    @Test
    fun `can register custom encoding`() {
        val encoding = DummyEncoding()
        registry.registerCustomEncoding(encoding)

        val retrievedEncoding = registry.getEncoding(encoding.name)
        retrievedEncoding.shouldNotBeNull()
        retrievedEncoding shouldBeEqualTo encoding
    }

    private class DummyEncoding: Encoding {
        override val name: String = "dummy"
        override fun encode(text: String): List<Int> = emptyList()
        override fun encode(text: String, maxTokens: Int): EncodingResult = EncodingResult()
        override fun encodeOrdinary(text: String): List<Int> = emptyList()
        override fun encodeOrdinary(text: String, maxTokens: Int): EncodingResult = EncodingResult()
        override fun countTokens(text: String): Int = 0
        override fun countTokensOrdinary(text: String): Int = 0
        override fun decode(tokens: List<Int>): String = ""
        override fun decodeBytes(tokens: List<Int>): ByteArray = emptyByteArray
    }
}
