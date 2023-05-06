package io.bluetape4k.collections.eclipse

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.eclipse.collections.impl.set.mutable.UnifiedSet
import org.junit.jupiter.api.Test

class UnifiedSetSupportTest {

    companion object: KLogging()

    @Test
    fun `verify empty unified set`() {
        val empty = emptyUnifiedSet<Int>()
        empty.isEmpty.shouldBeTrue()
    }

    @Test
    fun `create unified set`() {
        val set = unifiedSet(3) { 'a' + it }
        verifyUnifiedSet(set)

        verifyUnifiedSet(unifiedSetOf('a', 'b', 'c'))
    }

    @Test
    fun `create unified set from collection`() {
        val list = List(3) { 'a' + it }

        verifyUnifiedSet(list.toUnifiedSet())
        verifyUnifiedSet(list.asSequence().toUnifiedSet())
        verifyUnifiedSet(list.toTypedArray().toUnifiedSet())
    }

    private fun verifyUnifiedSet(set: UnifiedSet<Char>) {
        set.size shouldBeEqualTo 3
        set shouldBeEqualTo setOf('a', 'b', 'c')
    }
}
