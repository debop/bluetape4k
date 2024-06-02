package io.bluetape4k.tiktoken

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.tiktoken.api.Encoding
import io.bluetape4k.tiktoken.api.EncodingRegistry
import io.bluetape4k.tiktoken.api.EncodingType
import io.bluetape4k.tiktoken.api.GptBytePairEncodingParams
import io.bluetape4k.tiktoken.api.ModelType
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractEncodingRegistry: EncodingRegistry {

    companion object: KLogging()

    private val encodings = ConcurrentHashMap<String, Encoding>()

    override fun getEncoding(encodingName: String): Encoding? = encodings[encodingName]

    override fun getEncoding(encodingType: EncodingType): Encoding {
        return encodings[encodingType.encodingName]
            ?: throw IllegalArgumentException("No encoding found for type ${encodingType.encodingName}")
    }

    override fun getEncodingForModel(modelName: String): Encoding? {
        log.debug { "Get encoding for model. modelName=$modelName" }

        val modelType = ModelType.forModelName(modelName)
        if (modelType != null) {
            return getEncodingForModel(modelType)
        }

        if (modelName.startsWith(ModelType.GPT_4_32K.modelName)) {
            return getEncodingForModel(ModelType.GPT_4_32K)
        }
        if (modelName.startsWith(ModelType.GPT_4.modelName)) {
            return getEncodingForModel(ModelType.GPT_4)
        }
        if (modelName.startsWith(ModelType.GPT_3_5_TURBO_16K.modelName)) {
            return getEncodingForModel(ModelType.GPT_3_5_TURBO_16K)
        }
        if (modelName.startsWith(ModelType.GPT_3_5_TURBO.modelName)) {
            return getEncodingForModel(ModelType.GPT_3_5_TURBO)
        }

        return null
    }

    override fun getEncodingForModel(modelType: ModelType): Encoding {
        return encodings[modelType.encodingType.encodingName]
            ?: error("No encoding registered for model type ${modelType.modelName}")
    }

    override fun registerGptBytePairEncoding(parameters: GptBytePairEncodingParams): EncodingRegistry {
        return registerCustomEncoding(EncodingFactory.fromParameters(parameters))
    }

    override fun registerCustomEncoding(encoding: Encoding): EncodingRegistry {
        val encodingName = encoding.name
        val previousEncoding = encodings.putIfAbsent(encodingName, encoding)
        if (previousEncoding != null) {
            error("Encoding $encodingName already registered")
        }
        return this
    }

    protected fun addEncoding(encodingType: EncodingType) {
        if (encodings.contains(encodingType)) return

        when (encodingType) {
            EncodingType.R50K_BASE   ->
                encodings.computeIfAbsent(encodingType.encodingName) { EncodingFactory.r50kBase() }

            EncodingType.P50K_BASE   ->
                encodings.computeIfAbsent(encodingType.encodingName) { EncodingFactory.p50kBase() }

            EncodingType.P50K_EDIT   ->
                encodings.computeIfAbsent(encodingType.encodingName) { EncodingFactory.p50kEdit() }

            EncodingType.CL100K_BASE ->
                encodings.computeIfAbsent(encodingType.encodingName) { EncodingFactory.cl100kBase() }
            //            else                     ->
            //                error("Unknown encoding type: ${encodingType.encodingName}")
        }
    }
}
