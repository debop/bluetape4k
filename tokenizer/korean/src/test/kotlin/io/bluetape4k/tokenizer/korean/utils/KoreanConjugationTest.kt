package io.bluetape4k.tokenizer.korean.utils

import io.bluetape4k.logging.debug
import io.bluetape4k.logging.error
import io.bluetape4k.tokenizer.korean.TestBase
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test

class KoreanConjugationTest: TestBase() {

    @Test
    fun `should expand codas of verbs`() = runTest {
        assertConjudations("verb_conjugate.txt", false)
    }

    @Test
    fun `should expand codas of adjectives`() = runTest {
        assertConjudations("adj_conjugate.txt", true)
    }

    private suspend fun assertConjudations(filename: String, isAdjective: Boolean) {
        log.debug { "load file=[$filename], isAdjective=$isAdjective" }

        val input = KoreanDictionaryProvider.readWordsAsFlow(filename)
        val loaded: List<Pair<String, String>> = input
            .map {
                val sp = it.split("\t")
                Pair(sp[0], sp[1])
            }
            .toList()

        val result = loaded.fold(true) { output, (predicate, goldensetExpanded) ->
            val conjugated = KoreanConjugation.conjugatePredicatesToCharArraySet(hashSetOf(predicate), isAdjective)
            val matched = matchGoldenset(predicate, conjugated, goldensetExpanded)
            matched && output
        }
        result.shouldBeTrue()
    }

    private fun matchGoldenset(predicate: String, newExpanded: CharArraySet, examples: String): Boolean {

        val newExpandedString = newExpanded.map { String(it as CharArray) }.toList().sorted().joinToString(", ")
        val isSameToGoldenset = newExpandedString == examples

        if (!isSameToGoldenset) {
            val prevSet = examples.split(", ").toHashSet()
            val newSet = newExpandedString.split(", ").toHashSet()

            //      log.debug {
            //        "\nexamples=$examples" +
            //        "\nnewExpandedString=$newExpandedString" +
            //        "\nprevSet=$prevSet" +
            //        "\nnewSet =$newSet"
            //      }
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
