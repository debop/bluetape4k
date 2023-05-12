package io.bluetape4k.collections.enhanced

import io.bluetape4k.collections.eclipse.fastListOf
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class IndexedCollectionTest {

    companion object: KLogging()

    private fun decorateCollection(collection: MutableCollection<String>) =
        IndexedCollection.nonUniqueIndexed(collection) { it.toInt() }

    private fun decorateUniqueCollection(collection: MutableCollection<String>) =
        IndexedCollection.uniqueIndexed(collection) { it.toInt() }

    private fun makeTestCollection() =
        decorateCollection(fastListOf<String>())

    private fun makeUniqueTestCollection() =
        decorateUniqueCollection(fastListOf<String>())

    val fullElements = arrayOf("1", "2", "3", "4", "5", "6")
    val otherElements = arrayOf("9", "88", "678", "87", "98", "78", "99")

    private fun makeFullCollection() =
        decorateCollection(fullElements.toMutableList())

    private fun makeUniqueFullCollection() =
        decorateUniqueCollection(fullElements.toMutableList())

    @Test
    fun `add elements and retrieve by key`() {
        val indexed = makeTestCollection()
        indexed.add("12")
        indexed.add("16")
        indexed.add("1")
        indexed.addAll(listOf("2", "3", "4"))

        indexed.forEach {
            indexed[it.toInt()] shouldBeEqualTo it
        }
    }

    @Test
    fun `ensure duplicate element cause exception`() {
        val coll = makeUniqueTestCollection()

        coll.add("1")

        assertFailsWith<IllegalArgumentException> {
            coll.add("1")
        }
    }

    @Test
    fun `decorated collection is indexed on creation`() {
        val original = makeFullCollection()
        val indexed = decorateUniqueCollection(original)

        indexed.forEach {
            indexed[it.toInt()] shouldBeEqualTo it
        }
    }

    @Test
    fun `when collection is modified, update index`() {
        val original = fastListOf<String>()
        val indexed = decorateUniqueCollection(original)

        original.add("1")
        original.add("2")
        original.add("3")

        indexed[1].shouldBeNull()
        indexed[2].shouldBeNull()
        indexed[3].shouldBeNull()

        indexed.reindex()

        indexed[1] shouldBeEqualTo "1"
        indexed[2] shouldBeEqualTo "2"
        indexed[3] shouldBeEqualTo "3"
    }
}
