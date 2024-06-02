package io.bluetape4k.tokenizer.korean.utils

import io.bluetape4k.logging.debug
import io.bluetape4k.logging.error
import io.bluetape4k.tokenizer.korean.TestBase
import io.bluetape4k.tokenizer.utils.CharArraySet
import io.bluetape4k.tokenizer.utils.DictionaryProvider
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test

class KoreanConjugationTest: TestBase() {

    @Test
    fun `동사 활용`() = runTest {
        assertConjudations("$BASE_PATH/verb_conjugate.txt", false)
    }

    @Test
    fun `형용사 활용`() = runTest {
        assertConjudations("$BASE_PATH/adj_conjugate.txt", true)
    }

    private suspend fun assertConjudations(filename: String, isAdjective: Boolean) {
        log.debug { "load file=[$filename], isAdjective=$isAdjective" }

        val input = DictionaryProvider.readWordsAsSequence(filename)
        val loaded = input
            .map {
                val sp = it.split("\t")
                Pair(sp[0], sp[1])
            }
            .toList()
            .toMap()

        val result = loaded.all { (predicate, goldensetExpanded) ->
            val conjugated = KoreanConjugation.conjugatePredicatesToCharArraySet(setOf(predicate), isAdjective)
            val matched = matchGoldenset(predicate, conjugated, goldensetExpanded)
            matched
        }
        result.shouldBeTrue()
    }

    private fun matchGoldenset(predicate: String, newExpanded: CharArraySet, examples: String): Boolean {
        val newExpandedString = newExpanded.map { String(it as CharArray) }.toList().sorted().joinToString()
        val isSameToGoldenset = newExpandedString == examples

        if (!isSameToGoldenset) {
            val prevSet = examples.split(", ").toHashSet()
            val newSet = newExpandedString.split(", ").toHashSet()

            log.error {
                """
                |predicate=$predicate
                |${(prevSet - newSet).toList().sorted().joinToString(", ")},
                |${(newSet - prevSet).toList().sorted().joinToString(", ")}
                """.trimMargin()
            }
        }
        return isSameToGoldenset
    }
}
