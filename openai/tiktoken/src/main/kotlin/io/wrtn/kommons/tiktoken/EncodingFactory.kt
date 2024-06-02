package io.bluetape4k.tiktoken

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.error
import io.bluetape4k.tiktoken.api.Encoding
import io.bluetape4k.tiktoken.api.GptBytePairEncodingParams
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

object EncodingFactory: KLogging() {

    private const val ENDOFTEXT = "<|endoftext|>"
    private const val FIM_PREFIX = "<|fim_prefix|>"
    private const val FIM_MIDDLE = "<|fim_middle|>"
    private const val FIM_SUFFIX = "<|fim_suffix|>"
    private const val ENDOFPROMPT = "<|endofprompt|>"

    private const val FILE_PATH = "/io/bluetake4k/tiktoken"

    private val mergeableRanksCache = ConcurrentHashMap<String, Map<ByteArray, Int>>()

    private val SPECIAL_TOKENS_X50K_BASE: Map<String, Int> = mutableMapOf<String, Int>().apply {
        put(ENDOFTEXT, 50256)
    }

    private val SPECIAL_TOKENS_P50K_EDIT: Map<String, Int> = mutableMapOf<String, Int>().apply {
        put(ENDOFTEXT, 50256)
        put(FIM_PREFIX, 50281)
        put(FIM_MIDDLE, 50282)
        put(FIM_SUFFIX, 50283)
    }

    private val SPECIAL_TOKENS_CL100K_BASE: Map<String, Int> = mutableMapOf<String, Int>().apply {
        put(ENDOFTEXT, 100257)
        put(FIM_PREFIX, 100258)
        put(FIM_MIDDLE, 100259)
        put(FIM_SUFFIX, 100260)
        put(ENDOFPROMPT, 100276)
    }

    /**
     * Returns an {@link Encoding} instance for the r50k_base encoding.
     */
    fun r50kBase(): Encoding {
        return fromPredefinedParameters(
            "r50k_base",
            """'s|'t|'re|'ve|'m|'ll|'d| ?\p{L}+| ?\p{N}+| ?[^\s\p{L}\p{N}]+|\s+(?!\S)|\s+""",
            "$FILE_PATH/r50k_base.tiktoken",
            SPECIAL_TOKENS_X50K_BASE
        )
    }

    /**
     * Returns an [Encoding] instance for the p50k_base encoding.
     */
    fun p50kBase(): Encoding {
        return fromPredefinedParameters(
            "p50k_base",
            """'s|'t|'re|'ve|'m|'ll|'d| ?\p{L}+| ?\p{N}+| ?[^\s\p{L}\p{N}]+|\s+(?!\S)|\s+""",
            "$FILE_PATH/p50k_base.tiktoken",
            SPECIAL_TOKENS_X50K_BASE
        )
    }

    /**
     * Returns an [Encoding] instance for the p50k_edit encoding.
     */
    fun p50kEdit(): Encoding {
        return fromPredefinedParameters(
            "p50k_edit",
            """'s|'t|'re|'ve|'m|'ll|'d| ?\p{L}+| ?\p{N}+| ?[^\s\p{L}\p{N}]+|\s+(?!\S)|\s+""",
            "$FILE_PATH/p50k_base.tiktoken",
            SPECIAL_TOKENS_P50K_EDIT
        )
    }

    /**
     * Returns an [Encoding] instance for the cl100k_base encoding.
     */
    fun cl100kBase(): Encoding {
        return fromPredefinedParameters(
            "cl100k_base",
            """(?i:'s|'t|'re|'ve|'m|'ll|'d)|[^\r\n\p{L}\p{N}]?\p{L}+|\p{N}{1,3}| ?[^\s\p{L}\p{N}]+[\r\n]*|\s*[\r\n]+|\s+(?!\S)|\s+""",
            "$FILE_PATH/cl100k_base.tiktoken",
            SPECIAL_TOKENS_CL100K_BASE
        )
    }


    /**
     * Returns an {@link Encoding} instance for the given GPT BytePairEncoding parameters.
     *
     * @param parameters the GPT BytePairEncoding parameters
     * @return an {@link Encoding} instance for the given GPT BytePairEncoding parameters
     */
    fun fromParameters(parameters: GptBytePairEncodingParams): Encoding {
        return GptBytePairEncoding(parameters)
    }

    private fun fromPredefinedParameters(
        name: String,
        patternString: String,
        fileName: String,
        specialTokens: Map<String, Int>,
    ): Encoding {
        val regex = try {
            Pattern.compile(patternString, Pattern.UNICODE_CHARACTER_CLASS)
        } catch (exception: IllegalArgumentException) {
            // Workaround for Android where an IllegalArgumentException is thrown when using UNICODE_CHARACTER_CLASS
            Pattern.compile(patternString)
        }

        val params = GptBytePairEncodingParams(name, regex, loadMergeableRanks(fileName), specialTokens)
        return fromParameters(params)
    }

    private fun loadMergeableRanks(fileName: String): Map<ByteArray, Int> {
        val regex = "\\s+".toRegex()

        return mergeableRanksCache.computeIfAbsent(fileName) {
            log.debug { "loading mergeable ranks from $fileName" }

            EncodingFactory::class.java.getResourceAsStream(fileName).use { input ->
                check(input != null) { "Could not find $fileName in resources" }
                val mergeableRanks = hashMapOf<ByteArray, Int>()

                InputStreamReader(input, Charsets.UTF_8).buffered().use { reader ->
                    reader.lineSequence().forEach { line ->
                        runCatching {
                            val parts = regex.split(line, 2)
                            if (parts.size != 2) {
                                error("Invalid line in $fileName: $line")
                            }
                            val token = decoder.decode(parts[0].toByteArray())
                            val rank = parts[1].trim().toInt()
                            mergeableRanks[token] = rank
                        }.onFailure {
                            log.error(it) { "Error while loading mergeable ranks from $fileName" }
                        }
                    }
                }
                mergeableRanks
            }
        }
    }

    private val decoder = Base64.getDecoder()
}
