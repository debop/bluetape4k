package io.bluetape4k.utils.naivebayes

import io.bluetape4k.logging.KLogging
import java.io.Serializable
import java.time.LocalDate

abstract class AbstractNaiveBayesClassifierTest {

    companion object: KLogging()

    fun String.splitWords(): Sequence<String> =
        split(Regex("\\s"))
            .asSequence()
            .map { it.replace(Regex("[^A-Za-z]"), "").lowercase() }
            .filter { it.isNotEmpty() }

//    fun String.tokenize(): Sequence<String> =
//        KoreanTokenizer.tokenize(this)
//            .asSequence()
//            .filter { it.pos == KoreanPos.Noun || it.pos == KoreanPos.Adjective }
//            .map { it.text.toLowerCase() }

    data class Email(val message: String, val isSpam: Boolean)

    data class BankTransaction(
        val date: LocalDate,
        val amount: Double,
        val memo: String,
        val category: String? = null,
    ): Serializable
}
