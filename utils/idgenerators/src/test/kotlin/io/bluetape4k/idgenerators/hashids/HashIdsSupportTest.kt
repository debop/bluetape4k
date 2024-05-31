package io.bluetape4k.idgenerators.hashids

import io.bluetape4k.collections.asParallelStream
import io.bluetape4k.idgenerators.flake.Flake
import io.bluetape4k.idgenerators.snowflake.GlobalSnowflake
import io.bluetape4k.idgenerators.uuid.TimebasedUuidGenerator
import io.bluetape4k.junit5.concurrency.MultithreadingTester
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.trace
import io.bluetape4k.support.toLong
import io.bluetape4k.utils.Runtimex
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class HashIdsSupportTest {

    companion object: KLogging() {
        const val ITEM_SIZE = 1000
    }

    private val hashids = Hashids("wrtn.io")

    @Nested
    inner class UuidTest {

        private val uuidGenerator = TimebasedUuidGenerator()

        @Test
        fun `encode random UUID`() {
            repeat(ITEM_SIZE) {
                verifyUuidEncode(UUID.randomUUID())
            }
        }

        @Test
        fun `encode time based uuid`() {
            repeat(ITEM_SIZE) {
                verifyUuidEncode(uuidGenerator.nextUUID())
            }
        }

        @Test
        fun `encode time based uuid as parallel`() {
            val uuids = List(ITEM_SIZE) { uuidGenerator.nextUUID() }
            uuids.parallelStream()
                .forEach {
                    verifyUuidEncode(it)
                }
        }

        private fun verifyUuidEncode(uuid: UUID) {
            val encoded = hashids.encodeUUID(uuid)
            log.debug { "hashids=$encoded" }
            val decoded = hashids.decodeUUID(encoded)
            decoded shouldBeEqualTo uuid
        }

        @Test
        fun `정렬된 UUID에 대한 hashid는 정렬되지 않습니다`() {
            val uuids = List(ITEM_SIZE) { uuidGenerator.nextUUID() }
            val encodeds = uuids
                .map { hashids.encodeUUID(it) }
                .onEach { log.trace { it } }

            encodeds.sorted() shouldNotBeEqualTo encodeds
        }
    }

    @Nested
    inner class SnowflakeTest {
        private val snowflake = GlobalSnowflake()

        @Test
        fun `encode snowflake id`() {
            repeat(ITEM_SIZE) {
                val id = snowflake.nextId()
                verifySnowflakeId(id)
            }
        }

        @Test
        fun `encode snowflake id as parallel`() {
            snowflake
                .nextIds(ITEM_SIZE)
                .asParallelStream()
                .forEach {
                    verifySnowflakeId(it)
                }
        }

        @Test
        fun `encode flake id in multi threading`() {
            val map = ConcurrentHashMap<Long, Int>()
            MultithreadingTester()
                .numThreads(2 * Runtimex.availableProcessors)
                .roundsPerThread(ITEM_SIZE)
                .add {
                    val id = snowflake.nextId()
                    verifySnowflakeId(id)
                    map.putIfAbsent(id, 1).shouldBeNull()
                }
                .run()
        }

        private fun verifySnowflakeId(id: Long) {
            val encoded = hashids.encode(id)
            log.debug { "hashids=$encoded" }
            val decoded = hashids.decode(encoded)

            decoded.size shouldBeEqualTo 1
            decoded[0] shouldBeEqualTo id
        }
    }

    @Nested
    inner class FlakeTest {
        private val flake = Flake()

        @Test
        fun `encode flake id`() {
            repeat(ITEM_SIZE) {
                val id = flake.nextId().toLong()
                verifyFlakeId(id)
            }
        }

        @Test
        fun `encode flake id as parallel`() {
            flake
                .nextIds(ITEM_SIZE)
                .asParallelStream()
                .forEach {
                    verifyFlakeId(it.toLong())
                }
        }

        private fun verifyFlakeId(id: Long) {
            val encoded = hashids.encode(id)
            log.debug { "id=$id, hashids=$encoded" }
            val decoded = hashids.decode(encoded)

            decoded.size shouldBeEqualTo 1
            decoded[0] shouldBeEqualTo id
        }
    }
}
