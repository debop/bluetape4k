package io.bluetape4k.tiktoken

import io.bluetape4k.tiktoken.api.Encoding
import io.bluetape4k.tiktoken.api.EncodingType
import io.bluetape4k.tiktoken.api.ModelType

class LazyEncodingRegistry: AbstractEncodingRegistry() {

    override fun getEncoding(encodingType: EncodingType): Encoding {
        addEncoding(encodingType)
        return super.getEncoding(encodingType)
    }

    override fun getEncoding(encodingName: String): Encoding? {
        EncodingType.forEncodingName(encodingName)?.let { addEncoding(it) }
        return super.getEncoding(encodingName)
    }

    override fun getEncodingForModel(modelType: ModelType): Encoding {
        addEncoding(modelType.encodingType)
        return super.getEncodingForModel(modelType)
    }
}
