package io.bluetape4k.hyperscan.wrapper

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeGreaterThan
import org.junit.jupiter.api.Test
import java.util.*

class ExpressionTest {

    companion object: KLogging()

    @Test
    fun `compile expression to database`() {
        val expression = Expression("test", ExpressionFlag.CASELESS)
        expression.compile().use { db ->
            db.getSize() shouldBeGreaterThan 0
        }
    }

    @Test
    fun `compile expressions to database`() {
        val flags = EnumSet.of(ExpressionFlag.CASELESS, ExpressionFlag.SOM_LEFTMOST)
        val expressions = LinkedList<Expression>().apply {
            add(Expression("Te?st", flags))
            add(Expression("ist", flags))
        }

        expressions.compile().use { db ->
            db.getSize() shouldBeGreaterThan 0
        }
    }
}
