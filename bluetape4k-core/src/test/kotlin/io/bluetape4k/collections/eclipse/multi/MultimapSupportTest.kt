package io.bluetape4k.collections.eclipse.multi

import io.bluetape4k.collections.eclipse.fastListOf
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.eclipse.collections.api.multimap.Multimap
import org.junit.jupiter.api.Test

class MultimapSupportTest {

    companion object: KLogging() {
        private val ones = fastListOf("1", "one", "일")
        private val twos = fastListOf("2", "two")
    }

    data class User(val name: String, val age: Int)

    @Test
    fun `create multimap of list`() {
        val mmap = listMultimapOf<Int, String>().apply {
            putAll(1, ones)
            putAll(2, twos)
        }
        verifyMultimap(mmap)

        // 새로운 키와 값을 추가한다
        mmap.put(3, "3")
        mmap.put(3, "three")
        mmap[3] shouldBeEqualTo listOf("3", "three")
    }

    @Test
    fun `create mutable multimap from map`() {
        val smap = mapOf(1 to ones, 2 to twos).flatMap { (k, vs) -> vs.map { k to it } }

        val mmap = smap.toListMultimap()
        verifyMultimap(mmap)

        val mmap2 = smap.toMutableListMultimap { it }
        verifyMultimap(mmap2)
    }

    @Test
    fun `create multimap of set`() {
        val smap = setMultimapOf<Int, String>().apply {
            putAll(1, ones)
            putAll(2, twos)
        }
        verifyMultimap(smap)

        // 중복되는 값은 추가되지 않는다
        smap.put(2, "two")
        verifyMultimap(smap)

        smap.put(3, "3")
        smap.put(3, "three")
        smap.put(3, "three")
        smap[3] shouldBeEqualTo setOf("3", "three")
    }

    @Test
    fun `create mutable set multimap from map`() {
        val smap = mapOf(1 to ones, 2 to twos).flatMap { (k, vs) -> vs.map { k to it } }

        val mmap = smap.toMutableSetMultimap()
        verifyMultimap(mmap)
        mmap.put(1, "1")
        verifyMultimap(mmap)

        val mmap2 = smap.toMutableSetMultimap { it }
        verifyMultimap(mmap2)
        mmap2.put(2, "2")
        verifyMultimap(mmap2)
    }

    private fun verifyMultimap(mmap: Multimap<Int, String>) {
        mmap.size() shouldBeEqualTo 5
        mmap.keysView().size() shouldBeEqualTo 2
        mmap[1].toList() shouldBeEqualTo ones
        mmap[2].toList() shouldBeEqualTo twos
    }
}
