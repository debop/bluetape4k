package io.bluetape4k.hyperscan

import io.bluetape4k.hyperscan.wrapper.CompileErrorException
import io.bluetape4k.hyperscan.wrapper.Database
import io.bluetape4k.hyperscan.wrapper.Expression
import io.bluetape4k.hyperscan.wrapper.ExpressionFlag
import io.bluetape4k.hyperscan.wrapper.Scanner
import io.bluetape4k.hyperscan.wrapper.compileDatabaseOf
import io.bluetape4k.logging.KLogging
import java.io.Closeable
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Filters a list of java.util.regex.Pattern using hyperscan
 * Returns only potentially matching patterns.
 *
 * This is not thread-safe, use once per thread.
 *
 * It allows to use hyperscan to filter only potentially
 * matching Patterns from a large list of patterns.
 *
 * You can still use the the full feature set
 * provided by regular Java Pattern API with some
 * performance benefits from hyperscan
 *
 * This is similar to chimera, only with java APIs.
 */
class PatternFilter private constructor(
    private val expressions: List<Expression>,
    private val matchers: Array<Matcher?>,
    //Some obscure patterns cannot be handled by hyperscan PREFILTER, hence will never be filtered
    private val notFilterable: List<Matcher>,
): Closeable {

    companion object: KLogging() {

        /**
         * Create a pattern filter for the provided patterns
         * @param patterns Patterns to be filtered
         * @throws CompileErrorException in case the compilation of the hyperscan representation fails
         */
        @JvmStatic
        @Throws(CompileErrorException::class)
        operator fun invoke(patterns: List<Pattern>): PatternFilter {
            val matchers = Array<Matcher?>(patterns.size) { null }
            val expressions = arrayListOf<Expression>()
            val notFilterable = arrayListOf<Matcher>()
            var id = 0
            patterns.forEach { pattern ->
                val expression = mapToExpression(pattern, id)
                if (expression == null) {
                    //can't be compiled to expression -> not filterable
                    notFilterable.add(pattern.matcher(""))
                } else {
                    expressions.add(expression)
                    matchers[id] = pattern.matcher("")
                    id++
                }
            }

            return PatternFilter(expressions, matchers, notFilterable)
        }

        private val defaultFlags = EnumSet.of(
            ExpressionFlag.UTF8,
            ExpressionFlag.PREFILTER,
            ExpressionFlag.ALLOWEMPTY,
            ExpressionFlag.SINGLEMATCH
        )

        private fun mapToExpression(pattern: Pattern, id: Int): Expression? {
            val flags = defaultFlags.clone()
            if (pattern.hasFlag(Pattern.CASE_INSENSITIVE)) {
                flags.add(ExpressionFlag.CASELESS)
            }
            if (pattern.hasFlag(Pattern.MULTILINE)) {
                flags.add(ExpressionFlag.MULTILINE)
            }
            if (pattern.hasFlag(Pattern.DOTALL)) {
                flags.add(ExpressionFlag.DOTALL)
            }

            val expression = Expression(pattern.pattern(), flags, id)
            if (!expression.validate().isValid) {
                return null
            }
            return expression
        }

        private fun Pattern.hasFlag(flag: Int): Boolean = (this.flags() and flag) == flag
    }

    private val database: Database = compileDatabaseOf(expressions)
    private val scanner: Scanner = Scanner().apply { allocScratch(database) }

    /**
     * Filter the large list of patterns down to a small list of probable matches
     * You need to confirm those using the regular Matcher API (.find()/matches())
     * @param input Text to use to match Patterns
     * @return Matcher for the probably matching Patterns
     */
    fun filter(input: String): List<Matcher> {
        val results = mutableListOf<Matcher>()
        scanner
            .scan(database, input)
            .forEach { match ->
                results.add(matchers[match.matchedExpression.id!!]!!.reset(input))
            }

        notFilterable.forEach { results.add(it.reset(input)) }

        return results
    }

    override fun close() {
        runCatching { database.close() }
        runCatching { scanner.close() }
    }
}
