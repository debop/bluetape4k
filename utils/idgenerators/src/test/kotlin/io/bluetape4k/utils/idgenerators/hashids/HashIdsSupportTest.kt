package io.bluetape4k.utils.idgenerators.hashids

import io.bluetape4k.collections.stream.asParallelStream
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.utils.idgenerators.snowflake.DefaultSnowflake
import io.bluetape4k.utils.idgenerators.uuid.TimebasedUuidGenerator
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class HashIdsSupportTest {

    companion object : KLogging() {
        const val REPEAT_SIZE = 1000
    }

    private val hashids = Hashids()

    @Nested
    inner class UuidTest {

        private val uuidGenerator = TimebasedUuidGenerator()

        @Test
        fun `encode random UUID`() {
            repeat(REPEAT_SIZE) {
                verifyUuidEncode(UUID.randomUUID())
            }
        }

        @Test
        fun `encode time based uuid`() {
            repeat(REPEAT_SIZE) {
                verifyUuidEncode(uuidGenerator.nextUUID())
            }
        }

        @Test
        fun `encode time based uuid as parallel`() {
            val uuids = List(REPEAT_SIZE) { uuidGenerator.nextUUID() }
            uuids.parallelStream()
                .forEach {
                    verifyUuidEncode(it)
                }
        }

        private fun verifyUuidEncode(uuid: UUID) {
            val encoded = hashids.encodeUUID(uuid)
            val decoded = hashids.decodeUUID(encoded)
            decoded shouldBeEqualTo uuid
        }

        @Test
        fun `정렬된 UUID에 대한 hashid는 정렬되지 않습니다`() {
            val uuids = List(10) { uuidGenerator.nextUUID() }
            val encodeds = uuids.map { hashids.encodeUUID(it) }.onEach { log.debug { it } }
            encodeds.sorted() shouldNotBeEqualTo encodeds
        }
    }

    @Nested
    inner class SnowflakeTest {
        val snowflake = DefaultSnowflake()

        @Test
        fun `encode snowflake id`() {
            repeat(REPEAT_SIZE) {
                val id = snowflake.nextId()
                verifySnowflakeId(id)
            }
        }

        @Test
        fun `encode snowflake id as parallel`() {
            snowflake
                .nextIds(REPEAT_SIZE)
                .asParallelStream()
                .forEach {
                    verifySnowflakeId(it)
                }
        }

        private fun verifySnowflakeId(id: Long) {
            val encoded = hashids.encode(id)
            val decoded = hashids.decode(encoded)

            decoded.size shouldBeEqualTo 1
            decoded[0] shouldBeEqualTo id
        }
    }
}