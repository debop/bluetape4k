package io.bluetape4k.collections.eclipse

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.eclipse.collections.impl.map.mutable.UnifiedMap
import org.junit.jupiter.api.Test

class UnifiedMapSupportTest {

    companion object: KLogging()

    @Test
    fun `create unified map`() {
        val map = unifiedMapOf(1 to 'a', 2 to 'b', 3 to 'c')
        verifyUnifiedMap(map)
    }

    @Test
    fun `create unified map by pairs`() {
        val pairs = List(3) { it + 1 to 'a' + it }
        val map = unifiedMapOf(pairs)

        verifyUnifiedMap(map)
    }

    @Test
    fun `create unified map with capacity`() {
        val map = unifiedMapWithCapacity<Int, Char>(1)
        map[1] = 'a'
        map[2] = 'b'
        map[3] = 'c'

        verifyUnifiedMap(map)
    }

    @Test
    fun `convert map to unified map`() {
        val kmap = mapOf(1 to 'a', 2 to 'b', 3 to 'c')
        val map = kmap.toUnifiedMap()

        verifyUnifiedMap(map)
    }

    @Test
    fun `convert paris to unified map`() {
        val pairs = listOf(1 to 'a', 2 to 'b', 3 to 'c')
        val map = pairs.toUnifiedMap()

        verifyUnifiedMap(map)
    }

    private fun verifyUnifiedMap(map: UnifiedMap<Int, Char>) {
        map.size shouldBeEqualTo 3
        map[1] shouldBeEqualTo 'a'
        map[2] shouldBeEqualTo 'b'
        map[3] shouldBeEqualTo 'c'
    }
}
