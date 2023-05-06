package io.bluetape4k.utils.ahocorasick.trie

import java.io.Serializable

data class TrieConfig(
    var allowOverlaps: Boolean = true,
    var onlyWholeWords: Boolean = false,
    var onlyWholeWordsWhiteSpaceSeparated: Boolean = false,
    var ignoreCase: Boolean = false,
    var stopOnHit: Boolean = false,
): Serializable {

    companion object {
        val DEFAULT = TrieConfig()

        fun builder(): Builder = Builder()
    }

    class Builder {
        private var allowOverlaps: Boolean = true
        private var onlyWholeWords: Boolean = false
        private var onlyWholeWordsWhiteSpaceSeparated: Boolean = false
        private var ignoreCase: Boolean = false
        private var stopOnHit: Boolean = false

        fun allowOverlaps(value: Boolean = true) = apply {
            this.allowOverlaps = value
        }

        fun onlyWholeWords(value: Boolean = false) = apply {
            this.onlyWholeWords = value
        }

        fun onlyWholeWordsWhiteSpaceSeparated(value: Boolean = false) = apply {
            this.onlyWholeWordsWhiteSpaceSeparated = value
        }

        fun ignoreCase(value: Boolean = false) = apply {
            this.ignoreCase = value
        }

        fun stopOnHit(value: Boolean = false) = apply {
            this.stopOnHit = value
        }

        fun build(): TrieConfig {
            return TrieConfig(allowOverlaps, onlyWholeWords, onlyWholeWordsWhiteSpaceSeparated, ignoreCase, stopOnHit)
        }
    }
}
