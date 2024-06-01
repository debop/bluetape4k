package io.bluetape4k.hyperscan.wrapper

import io.bluetape4k.io.toInputStream
import io.bluetape4k.junit5.folder.TempFolder
import io.bluetape4k.junit5.folder.TempFolderTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldHaveSize
import org.apache.commons.io.output.ByteArrayOutputStream
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.test.assertFailsWith

@TempFolderTest
class ScannerIntegrationTest {

    companion object: KLogging() {
        private fun roundTrip(db: Database, serialize: SerializeDatabase): Database {
            if (serialize == SerializeDatabase.DONT_SERIALIZE) {
                return db
            }
            ByteArrayOutputStream().use { bos ->
                db.save(bos)
                db.close()

                val deserialized = loadDatabase(bos.toByteArray().toInputStream())

                deserialized shouldBeEqualTo db
                return deserialized
            }
        }

        private fun builExpressions(
            expressionStrings: List<String>,
            flags: List<EnumSet<ExpressionFlag>>,
        ): List<Expression> {
            return expressionStrings.mapIndexed { index, expr ->
                Expression(expr, flags[index])
            }
        }

    }

    enum class SerializeDatabase {
        DONT_SERIALIZE,
        SERIALIZE
    }

    @ParameterizedTest(name = "serialize={0}")
    @EnumSource(SerializeDatabase::class)
    fun `simple single expression`(serialize: SerializeDatabase) {
        val flags = EnumSet.of(ExpressionFlag.CASELESS, ExpressionFlag.SOM_LEFTMOST)
        val expression = Expression("Te?st", flags)
        val result = expression.validate()
        result.isValid.shouldBeTrue()
        result.message.shouldBeNull()

        val db = roundTrip(compileDatabaseOf(expression), serialize)
        db.getSize() shouldBeGreaterThan 0

        val scanner = Scanner()
        scanner.allocScratch(db)
        val matches = scanner.scan(db, "Dies ist ein Test tst.").toList()
        matches.size shouldBeEqualTo 2
        with(matches[0]) {
            endPosition shouldBeEqualTo 16
        }
    }

    @ParameterizedTest(name = "serialize={0}")
    @EnumSource(SerializeDatabase::class)
    fun `simple multiple expression`(serialize: SerializeDatabase) {
        val flags = EnumSet.of(ExpressionFlag.CASELESS, ExpressionFlag.SOM_LEFTMOST)
        val expressions = LinkedList<Expression>().apply {
            add(Expression("Te?st", flags))
            add(Expression("ist", flags))
        }

        val db = roundTrip(compileDatabaseOf(expressions), serialize)
        db.getSize() shouldBeGreaterThan 0

        val scanner = Scanner().apply {
            allocScratch(db)
        }

        val matches = scanner.scan(db, "Dies ist ein Test tst.").toList()
        matches shouldHaveSize 3
        with(matches.first()) {
            startPosition shouldBeEqualTo 5
            endPosition shouldBeEqualTo 7
            matchedString shouldBeEqualTo "ist"
            matchedExpression shouldBeEqualTo expressions[1]
        }
        log.debug { "scanner size=${scanner.getSize()}" }
        scanner.getSize() shouldBeGreaterThan 0
    }

    @ParameterizedTest(name = "serialize={0}")
    @EnumSource(SerializeDatabase::class)
    fun `expression with id`(serialize: SerializeDatabase) {
        val exprId = 42
        val db = roundTrip(compileDatabaseOf(Expression("test", id = exprId)), serialize)

        val scanner = Scanner().apply {
            allocScratch(db)
        }

        val matches = scanner.scan(db, "12345 test string").toList()

        matches shouldHaveSize 1
        with(matches.first()) {
            matchedExpression.id shouldBeEqualTo exprId
        }
    }

    @ParameterizedTest(name = "serialize={0}")
    @EnumSource(SerializeDatabase::class)
    fun `infinite regex`(serialize: SerializeDatabase) {
        val expr = Expression("a|", ExpressionFlag.ALLOWEMPTY)
        val db = roundTrip(compileDatabaseOf(expr), serialize)

        val scanner = Scanner().apply {
            allocScratch(db)
        }

        val input = "12345 test string"
        val matches = scanner.scan(db, input).toList()
        matches shouldHaveSize input.length + 1
    }

    @Test
    fun `blank expression`() {
        assertFailsWith<IllegalArgumentException> {
            compileDatabaseOf(Expression(""))
        }
        assertFailsWith<IllegalArgumentException> {
            compileDatabaseOf(Expression(" "))
        }
        assertFailsWith<IllegalArgumentException> {
            compileDatabaseOf(Expression("\t"))
        }
    }

    @ParameterizedTest(name = "serialize={0}")
    @EnumSource(SerializeDatabase::class)
    fun `empty string match`(serialize: SerializeDatabase) {
        val expr = Expression(".*", ExpressionFlag.ALLOWEMPTY)
        val db = roundTrip(compileDatabaseOf(expr), serialize)

        val scanner = Scanner().apply {
            allocScratch(db)
        }

        val input = ""
        val matches = scanner.scan(db, input).toList()
        matches shouldHaveSize 1
        matches.first().matchedString shouldBeEqualTo ""
    }

    @ParameterizedTest(name = "serialize={0}")
    @EnumSource(SerializeDatabase::class)
    fun `empty string no match`(serialize: SerializeDatabase) {
        val expr = Expression(".+", ExpressionFlag.ALLOWEMPTY)
        val db = roundTrip(compileDatabaseOf(expr), serialize)

        val scanner = Scanner().apply {
            allocScratch(db)
        }

        val input = ""
        val matches = scanner.scan(db, input).toList()
        matches.shouldBeEmpty()
    }

    @ParameterizedTest(name = "serialize={0}")
    @EnumSource(SerializeDatabase::class)
    fun `readme example`(serialize: SerializeDatabase, tempFolder: TempFolder) {
        // we define a list containing of our expressions
        val expressions = mutableListOf<Expression>()

        //the first argument in the constructor is the regular pattern, the latter one is a expression flag
        //make sure you read the original hyperscan documentation to learn more about flags
        //or browse the ExpressionFlag.java in this repo.
        expressions.add(Expression("[0-9]{5}", ExpressionFlag.SOM_LEFTMOST))
        expressions.add(Expression("Test", ExpressionFlag.CASELESS))

        //we precompile the expression into a database.
        //you can compile single expression instances or lists of expressions

        //since we're interacting with native handles always use try-with-resources or call the close method after use
        compileDatabaseOf(expressions).use { db ->
            //initialize scanner - one scanner per thread!
            //same here, always use try-with-resources or call the close method after use
            Scanner().use { scanner ->
                // allocate scratch space for the scanner
                scanner.allocScratch(db)

                //provide the database and the input string
                //returns a list with matches
                //synchronized method, only one execution at a time (use more scanner instances for multithreading)
                val matches = scanner.scan(db, "12345 test string")

                //matches always contain the expression causing the match and the end position of the match
                //the start position and the matches string it self is only part of a matach if the
                //SOM_LEFTMOST is set (for more details refer to the original hyperscan documentation)
            }

            // Save the database to the file system for later use
            val file = tempFolder.createFile("db")
            FileOutputStream(file).use { out ->
                db.save(out)
            }

            // Later, load the database back in. This is useful for large databases that take a long time to compile.
            // You can compile them offline, save them to a file, and then quickly load them in at runtime.
            // The load has to happen on the same type of platform as the save.
            FileInputStream(file).use { input ->
                val loadedDb = loadDatabase(input)
            }
        }
    }

    @ParameterizedTest(name = "serialize={0}")
    @EnumSource(SerializeDatabase::class)
    fun `korean UTF8`(serialize: SerializeDatabase) {
        val expr = Expression("테스트", ExpressionFlag.UTF8)

        roundTrip(compileDatabaseOf(expr), serialize).use { db ->
            scannerOf(db).use { scanner ->
                val matches = scanner.scan(db, "하이퍼스캔 테스트입니다").toList()

                matches shouldHaveSize 1
                log.debug { "match=${matches.first()}" }
            }
        }
    }

    @ParameterizedTest(name = "serialize={0}")
    @EnumSource(SerializeDatabase::class)
    fun `utf8 matched string`(serialize: SerializeDatabase) {
        val expr = Expression("\\d{5}", EnumSet.of(ExpressionFlag.SOM_LEFTMOST, ExpressionFlag.UTF8))
        roundTrip(expr.compile(), serialize).use { db ->
            scannerOf(db).use { scanner ->
                val input = "58744 78524 \uD83D\uDE00The quick brown fox ◌\uD804\uDD00 jumps 06840 over the lazy dog༼؈"
                val matches = scanner.scan(db, input).toList()
                matches.forEachIndexed { index, match ->
                    log.debug { "match[$index]=$match" }
                }

                with(matches[2]) {
                    matchedString shouldBeEqualTo "06840"
                    startPosition shouldBeEqualTo 44
                    endPosition shouldBeEqualTo 48
                }
            }
        }
    }

    @ParameterizedTest(name = "serialize={0}")
    @EnumSource(SerializeDatabase::class)
    fun `logical combination`(serialize: SerializeDatabase) {
        val expressionStrings = listOf(
            "abc",
            "def",
            "foobar.*gh",
            "teakettle{4,10}",
            "ijkl[mMn]",
            "(0 & 1 & 2) | (3 & !4)",
            "(0 | 1 & 2) & (!3 | 4)",
            "((0 | 1) & 2) & (3 | 4)"
        )
        val flags = listOf(
            EnumSet.of(ExpressionFlag.QUIET),
            EnumSet.of(ExpressionFlag.QUIET),
            EnumSet.of(ExpressionFlag.QUIET),
            EnumSet.of(ExpressionFlag.NO_FLAG),
            EnumSet.of(ExpressionFlag.QUIET),
            EnumSet.of(ExpressionFlag.COMBINATION),
            EnumSet.of(ExpressionFlag.COMBINATION),
            EnumSet.of(ExpressionFlag.COMBINATION)
        )

        val expressions = buildExpressions(expressionStrings, flags)

        roundTrip(expressions.compile(), serialize).use { db ->
            scannerOf(db).use { scanner ->
                val input = "abbdefxxfoobarrrghabcxdefxteakettleeeeexxxxijklmxxdef"
                val matches = scanner.scan(db, input).toList()
                matches shouldHaveSize 17

                assertMatch(17, expressionStrings[6], matches[0])
                assertMatch(20, expressionStrings[5], matches[1])
                assertMatch(20, expressionStrings[6], matches[2])
                assertMatch(24, expressionStrings[5], matches[3])
                assertMatch(24, expressionStrings[6], matches[4])
                assertMatch(37, expressionStrings[3], matches[5])
                assertMatch(37, expressionStrings[5], matches[6])
                assertMatch(37, expressionStrings[7], matches[7])
                assertMatch(38, expressionStrings[3], matches[8])
                assertMatch(38, expressionStrings[5], matches[9])
                assertMatch(38, expressionStrings[7], matches[10])
                assertMatch(47, expressionStrings[5], matches[11])
                assertMatch(47, expressionStrings[6], matches[12])
                assertMatch(47, expressionStrings[7], matches[13])
                assertMatch(52, expressionStrings[5], matches[14])
                assertMatch(52, expressionStrings[6], matches[15])
                assertMatch(52, expressionStrings[7], matches[16])
            }
        }
    }

    private fun buildExpressions(
        expressionStrings: List<String>,
        flags: List<EnumSet<ExpressionFlag>>,
    ): List<Expression> {
        return expressionStrings.mapIndexed { index, expr ->
            Expression(expr, flags[index])
        }
    }


    private fun assertMatch(expectedEndPosition: Int, expectedExpression: String, actualMatch: Match) {
        actualMatch.endPosition shouldBeEqualTo expectedEndPosition
        actualMatch.matchedExpression.expression shouldBeEqualTo expectedExpression
    }

}
