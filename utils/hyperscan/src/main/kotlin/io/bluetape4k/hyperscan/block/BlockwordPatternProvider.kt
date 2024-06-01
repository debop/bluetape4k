package io.bluetape4k.hyperscan.block

import io.bluetape4k.hyperscan.utils.DictionaryProvider
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import java.util.regex.Pattern

/**
 * 금칙어 문자을 추출하는 패턴을 제공합니다.
 */
object BlockwordPatternProvider: KLogging() {

    private const val BASE_PATH = "block"
    private const val BLOCK_PATTERNS = "block_patterns.txt"

    val blockPatterns: List<Pattern> by lazy {
        log.info { "Load Blockword patterns from $BASE_PATH/$BLOCK_PATTERNS" }
        DictionaryProvider.loadFromResource("$BASE_PATH/$BLOCK_PATTERNS")
            .map { Pattern.compile(it) }
            .toList()
    }

    val blockRegex: List<Regex> by lazy {
        log.info { "Load Blockword patterns from $BASE_PATH/$BLOCK_PATTERNS" }
        DictionaryProvider.loadFromResource("$BASE_PATH/$BLOCK_PATTERNS")
            .map { Regex(it) }
            .toList()
    }
}
