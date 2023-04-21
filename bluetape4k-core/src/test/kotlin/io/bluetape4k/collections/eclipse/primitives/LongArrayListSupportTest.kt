package io.bluetape4k.collections.eclipse.primitives

import io.bluetape4k.collections.longSequence
import io.bluetape4k.collections.toList
import java.util.stream.LongStream
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class LongArrayListSupportTest {

    private val kotlinList = List(5) { it + 1L }
    private val kotlinSet = kotlinList.toSet()
    private val expectedArray = longArrayOf(1, 2, 3, 4, 5)
    private val expectedArrayList = longArrayListOf(1, 2, 3, 4, 5)

    @Test
    fun `kotlin array to eclopse array`() {
        val kotlinArray = expectedArray
        val eclipseArray = expectedArrayList

        kotlinArray.toLongArrayList() shouldBeEqualTo eclipseArray
    }

    @Test
    fun `sequence to primitive array list`() {
        val array = longSequence(1, 5).take(5).toLongArrayList()
        array shouldBeEqualTo expectedArrayList
    }

    @Test
    fun `iterable to primitive array list`() {
        val array = kotlinList.toLongArrayList()
        array shouldBeEqualTo expectedArrayList
    }

    @Test
    fun `stream to primitive array list`() {
        val array = LongStream.range(1, 6).toLongArrayList()
        array shouldBeEqualTo expectedArrayList
    }

    @Test
    fun `convert primitive array list`() {
        val array = longArrayList(5) { it + 1L }
        array.size() shouldBeEqualTo 5
        array shouldBeEqualTo expectedArrayList
    }

    @Test
    fun `primitive list asList`() {
        val list = longArrayListOf(1, 2, 3, 4, 5).asList()
        list.size shouldBeEqualTo 5
        list shouldBeEqualTo kotlinList
    }

    @Test
    fun `primitive set asSet`() {
        val set = longArrayListOf(1, 2, 2, 3, 3, 4, 5).asSet()
        set.size shouldBeEqualTo 5
        set shouldBeEqualTo kotlinSet
    }

    @Test
    fun `primitive array list to list`() {

        val expected = listOf<Long>(1, 2, 3, 4, 4, 5)
        val array = longArrayListOf(1, 2, 3, 4, 4, 5)

        array.toArray() shouldBeEqualTo expected.toLongArray()

        array.asIterable().toList() shouldBeEqualTo expected
        array.asSequence().toList() shouldBeEqualTo expected

        array.asIterator().toList() shouldBeEqualTo expected

        array.asList() shouldBeEqualTo expected
        array.asSet() shouldBeEqualTo expected.toSet()
    }

    @Test
    fun `get product`() {
        longArrayListOf(1, 3, 5).product() shouldBeEqualTo (1 * 3 * 5).toDouble()
        longArrayListOf(-1, -3, -5).product() shouldBeEqualTo (-1 * -3 * -5).toDouble()
    }
}
