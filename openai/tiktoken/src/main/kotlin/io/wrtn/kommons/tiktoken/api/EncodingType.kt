package io.bluetape4k.tiktoken.api

enum class EncodingType(val encodingName: String) {

    R50K_BASE("r50k_base"),
    P50K_BASE("p50k_base"),
    P50K_EDIT("p50k_edit"),
    CL100K_BASE("cl100k_base");

    companion object {

        private val encodingNameToEncodingType: Map<String, EncodingType> =
            entries.associateBy { it.encodingName }

        @JvmStatic
        fun forEncodingName(encodingName: String): EncodingType? {
            return encodingNameToEncodingType[encodingName.lowercase()]
        }
    }
}
