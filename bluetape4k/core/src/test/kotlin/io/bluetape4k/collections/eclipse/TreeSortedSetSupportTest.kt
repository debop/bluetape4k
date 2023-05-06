package io.bluetape4k.collections.eclipse

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet
import org.junit.jupiter.api.Test

class TreeSortedSetSupportTest {

    companion object: KLogging()

    @Test
    fun `verify empty tree sorted set`() {
        val empty = emptyTreeSortedSet<Int>()
        empty.isEmpty.shouldBeTrue()
    }

    @Test
    fun `create tree sorted set`() {
        val set = treeSortedSet(3) { 'c' - it }
        verifyTreeSortedSet(set)

        verifyTreeSortedSet(treeSortedSetOf('c', 'b', 'a'))
    }

    @Test
    fun `create tree sorted from collection`() {
        val list = List(3) { 'a' + it }

        verifyTreeSortedSet(list.toTreeSortedSet())
        verifyTreeSortedSet(list.asSequence().toTreeSortedSet())
        verifyTreeSortedSet(list.toTypedArray().toTreeSortedSet())
    }


    private fun verifyTreeSortedSet(set: TreeSortedSet<Char>) {
        set.size shouldBeEqualTo 3
        set shouldBeEqualTo setOf('a', 'b', 'c')
    }
}
