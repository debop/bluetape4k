package io.bluetape4k.workshop.redis.cluster.basic

import io.bluetape4k.workshop.redis.cluster.AbstractRedisClusterTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisOperations

class BasicUsageTest(
    @Autowired private val operations: RedisOperations<String, String>,
): AbstractRedisClusterTest() {

    @BeforeEach
    fun beforeEach() {
        operations.execute { conn ->
            conn.flushDb()
        }
    }

    /**
     * Operation executed on a single node and slot.
     *
     * ```
     * -> SLOT 5798 served by 127.0.0.1:30002
     * ```
     */
    @Test
    fun `single slot operation`() {
        val key = "name"
        val value = randomValue()

        with(operations.opsForValue()) {
            set(key, value) // slot 5798
            get(key) shouldBeEqualTo value
        }
    }

    /**
     * Operation executed on a multiple nodes and slots.
     *
     * ```
     * -> SLOT 5798 served by 127.0.0.1:30002
     * -> SLOT 14594 served by 127.0.0.1:30003
     * ```
     */
    @Test
    fun `multi slot operations`() {
        val key1 = "name"
        val key2 = "nickname"
        val value1 = randomValue()
        val value2 = randomValue()

        with(operations.opsForValue()) {
            set(key1, value1) // slot 5798
            set(key2, value2) // slot 14594

            multiGet(listOf(key1, key2))!! shouldContainSame listOf(value1, value2)
        }
    }

    /**
     * Operation executed on a single node and slot because of pinned slot key.
     *
     * ```
     * -> SLOT 5798 served by 127.0.0.1:30002
     * ```
     */
    @Test
    fun `fixed slot operation`() {
        val key1 = "{user}.name"
        val key2 = "{user}.nickname"
        val value1 = randomValue()
        val value2 = randomValue()

        with(operations.opsForValue()) {
            set(key1, value1) // slot 5798
            set(key2, value2) // slot 5798

            multiGet(listOf(key1, key2))!! shouldContainSame listOf(value1, value2)
        }
    }

    /**
     * Operation executed across the cluster to retrieve cumulated result.
     *
     * ```
     * -> KEY age served by 127.0.0.1:30001
     * -> KEY name served by 127.0.0.1:30002
     * -> KEY nickname served by 127.0.0.1:30003
     * ```
     */
    @Test
    fun `multi node operations`() {
        val key1 = "name"
        val key2 = "nickname"
        val key3 = "age"
        val value1 = randomValue()
        val value2 = randomValue()
        val value3 = randomValue()

        with(operations.opsForValue()) {
            set(key1, value1) // slot 5798
            set(key2, value2) // slot 14594
            set(key3, value3) // slot 741

            multiGet(listOf(key1, key2, key3))!! shouldContainSame listOf(value1, value2, value3)
        }

        operations.keys("*")!! shouldContainAll listOf(key1, key2, key3)
    }
}
