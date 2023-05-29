package io.bluetape4k.tokenizer.korean.utils

import io.bluetape4k.collections.eclipse.emptyFastList
import io.bluetape4k.collections.eclipse.fastListOf
import io.bluetape4k.collections.eclipse.unifiedSetOf
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Adjective
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Adverb
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Alpha
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.CashTag
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Conjunction
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Determiner
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Email
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Eomi
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Exclamation
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Foreign
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Hashtag
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Josa
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Korean
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.KoreanParticle
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Modifier
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Noun
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Number
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Others
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.PreEomi
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Punctuation
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.ScreenName
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Suffix
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.URL
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Verb
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.VerbPrefix
import java.io.Serializable


object KoreanPosx: Serializable {

    val OtherPoses = hashSetOf(
        Korean,
        Foreign,
        Number,
        KoreanParticle,
        Alpha,
        Punctuation,
        Hashtag,
        ScreenName,
        Email,
        URL,
        CashTag
    )

    val shortCut = hashMapOf(
        'N' to Noun,
        'V' to Verb,
        'J' to Adjective,
        'A' to Adverb,
        'D' to Determiner,
        'E' to Exclamation,
        'C' to Conjunction,

        'j' to Josa,
        'e' to Eomi,
        'r' to PreEomi,
        'm' to Modifier,
        'v' to VerbPrefix,
        's' to Suffix,

        'a' to Alpha,
        'n' to Number,

        'o' to Others
    )

    val SelfNode = KoreanPosTrie(curPos = null, nextTrie = null, ending = null)

    fun buildTrie(s: String, endingPos: KoreanPos): List<KoreanPosTrie> {

        fun isFinal(rest: String): Boolean {
            fun isNextOptional(): Boolean = rest.fold(true) { output, c ->
                if (c == '+' || c == '1') false
                else output
            }
            return rest.isEmpty() || isNextOptional()
        }

        if (s.length < 2) {
            return emptyFastList()
        }

        val pos = shortCut[s[0]]
        val rule = s[1]
        val rest = s.slice(2 until s.length) // if (s.length > 1) s.slice(2 until s.length) else ""
        val end = if (isFinal(rest)) endingPos else null

        return when (rule) {
            '+'  -> fastListOf(
                KoreanPosTrie(
                    pos,
                    fastListOf<KoreanPosTrie>().apply {
                        add(SelfNode)
                        addAll(buildTrie(rest, endingPos))
                    },
                    end
                )
            )

            '*'  -> fastListOf<KoreanPosTrie>().apply {
                add(KoreanPosTrie(pos, fastListOf(SelfNode) + buildTrie(rest, endingPos), end))
                addAll(buildTrie(rest, endingPos))
            }

            '1'  -> fastListOf(KoreanPosTrie(pos, buildTrie(rest, endingPos), end))
            '0'  -> fastListOf<KoreanPosTrie>().apply {
                add(KoreanPosTrie(pos, buildTrie(rest, endingPos), end))
                addAll(buildTrie(rest, endingPos))
            }

            else -> error("Not supported rule. only support [+, *, 1, 0]")
        }
    }

    internal fun getTrie(sequences: Map<String, KoreanPos>): List<KoreanPosTrie> {
        val results = fastListOf<KoreanPosTrie>()
        sequences.forEach { (key, value) ->
            results.addAll(0, buildTrie(key, value))
        }
        return results
    }

    val Predicates = unifiedSetOf(Verb, Adjective)
}
