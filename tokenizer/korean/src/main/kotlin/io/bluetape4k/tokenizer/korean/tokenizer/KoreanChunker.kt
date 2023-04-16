package io.bluetape4k.tokenizer.korean.tokenizer

import io.bluetape4k.logging.KLogging
import io.bluetape4k.tokenizer.korean.isSpaceChar
import io.bluetape4k.tokenizer.korean.utils.KoreanPos
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Alpha
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.CashTag
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Email
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Foreign
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Hashtag
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Korean
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.KoreanParticle
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Number
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Punctuation
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.ScreenName
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Space
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.URL
import java.io.Serializable
import java.util.regex.Matcher

/**
 * Split input text into Korean Chunks (어절)
 */
object KoreanChunker: KLogging(), Serializable {

    val POS_PATTERNS = hashMapOf(
        Korean to """([가-힣]+)""".toRegex().toPattern(),
        Alpha to """(\p{Alpha}+)""".toRegex().toPattern(),
        Number to ("""(\$?\p{Digit}+""" +
            """(,\p{Digit}{3})*([/~:\.-]\p{Digit}+)?""" +
            """(천|만|억|조)*(%|원|달러|위안|옌|엔|유로|등|년|월|일|회|시간|시|분|초)?)""").toRegex().toPattern(),
        KoreanParticle to """([ㄱ-ㅣ]+)""".toRegex().toPattern(),
        Punctuation to """([\p{Punct}·…’]+)""".toRegex().toPattern(),
        URL to com.twitter.twittertext.Regex.VALID_URL,
        Email to """([\p{Alnum}.\-_]+@[\p{Alnum}\.]+)""".toRegex().toPattern(),
        Hashtag to com.twitter.twittertext.Regex.VALID_HASHTAG,
        ScreenName to com.twitter.twittertext.Regex.VALID_MENTION_OR_LIST,
        CashTag to com.twitter.twittertext.Regex.VALID_CASHTAG,
        Space to """\s+""".toRegex().toPattern()
    )

    val CHUNKING_ORDER = listOf(
        URL,
        Email,
        ScreenName,
        Hashtag,
        CashTag,
        Number,
        Korean,
        KoreanParticle,
        Alpha,
        Punctuation
    )

    fun getChunks(input: String, keepSpace: Boolean = true): List<String> =
        chunk(input).map { if (keepSpace) it.text else it.text.trim() }.toList()

    data class ChunkMatch(val start: Int, val end: Int, val text: String, val pos: KoreanPos) {

        fun disjoint(that: ChunkMatch): Boolean =
            (that.start < this.start && that.end <= this.start) ||
                (that.start >= this.end && that.end > this.end)
    }


    private fun splitBySpaceKeepingSpace(s: CharSequence): List<String> {
        val space = POS_PATTERNS[Space]!!
        val m = space.matcher(s)

        val tokens = arrayListOf<String>()
        var index = 0

        while (m.find()) {
            if (index < m.start()) {
                tokens.add(s.subSequence(index, m.start()).toString())
            }
            tokens.add(s.subSequence(m.start(), m.end()).toString())
            index = m.end()
        }

        if (index < s.length) {
            tokens.add(s.subSequence(index, s.length).toString())
        }

        return tokens
    }

    /**
     * Recursively call m.find() to find all the matches.
     * Use tail-recursion optimization to avoid stack overflow.
     *
     * @param m input Matcher
     * @param pos KoreanPos to attach
     * @param matches ouput list of ChunkMatch
     * @return list of ChunkMatches
     */
    tailrec fun findAllPatterns(
        m: Matcher,
        pos: KoreanPos,
        matches: MutableList<ChunkMatch> = mutableListOf(),
    ): List<ChunkMatch> {
        return if (m.find()) {
            matches.add(0, ChunkMatch(m.start(), m.end(), m.group(), pos))
            findAllPatterns(m, pos, matches)
        } else {
            matches
        }
    }

    private fun splitChunks(text: String): List<ChunkMatch> =
        if (text.isNotEmpty() && text[0].isSpaceChar) {
            arrayListOf(ChunkMatch(0, text.length, text, Space))
        } else {
            val chunksBuf = arrayListOf<ChunkMatch>()
            var matchedLen = 0
            CHUNKING_ORDER.forEach { pos ->
                if (matchedLen < text.length) {
                    val m: Matcher = POS_PATTERNS[pos]!!.matcher(text)
                    while (m.find()) {
                        val cm = ChunkMatch(m.start(), m.end(), m.group(), pos)
                        if (chunksBuf.all { cm.disjoint(it) }) {
                            chunksBuf += cm
                            matchedLen += cm.end - cm.start
                        }
                    }
                }
            }

            val sorted = chunksBuf.sortedBy { it.start }
            fillInUnmatched(text, sorted, Foreign)
        }

    /**
     * Fill in unmatched segments with given pos
     *
     * @param text input text
     * @param chunks matched chunks
     * @param pos KoreanPos to attach to the unmatched chunk
     * @return list of ChunkMatches
     */
    private fun fillInUnmatched(
        text: String,
        chunks: List<ChunkMatch>,
        pos: KoreanPos,
    ): List<ChunkMatch> {
        val chunksWithForeign = arrayListOf<ChunkMatch>()
        var prevEnd = 0

        chunks.forEach { cm ->
            prevEnd = when {
                cm.start == prevEnd -> {
                    chunksWithForeign.add(0, cm)
                    cm.end
                }

                cm.start > prevEnd -> {
                    chunksWithForeign.add(0, ChunkMatch(prevEnd, cm.start, text.slice(prevEnd until cm.start), pos))
                    chunksWithForeign.add(0, cm)
                    cm.end
                }

                else -> {
                    error("Non-disjoint chunk matches found. cm=$cm")
                }
            }
        }

        if (prevEnd < text.length) {
            chunksWithForeign.add(0, ChunkMatch(prevEnd, text.length, text.slice(prevEnd until text.length), pos))
        }

        return chunksWithForeign.reversed()
    }

    /**
     * Get chunks by given pos.
     *
     * @param input input string
     * @param pos one of supported KoreanPos's: URL, Email, ScreenName, Hashtag,
     *            CashTag, Korean, KoreanParticle, Number, Alpha, Punctuation
     * @return sequence of Korean chunk strings
     */
    fun getChunksByPos(input: String, pos: KoreanPos): List<KoreanToken> =
        chunk(input).filter { it.pos == pos }

    /**
     * Split input text into a sequnce of KoreanToken. A candidate for Korean parser
     * gets tagged with KoreanPos.Korean.
     *
     * @param input input string
     * @return sequence of KoreanTokens
     */
    fun chunk(input: CharSequence): List<KoreanToken> {
        val s = input.toString()

        // fold 대신 forEach 구문을 이용하여, 메모리를 절약하도록 했다
        val tokens = arrayListOf<KoreanToken>()
        var i = 0
        splitBySpaceKeepingSpace(s)
            .flatMap { splitChunks(it) }
            .forEach { m ->
                val segStart = s.indexOf(m.text, i)
                tokens.add(0, KoreanToken(m.text, m.pos, segStart, m.text.length))
                i = segStart + m.text.length
            }

        return tokens.reversed()
    }
}