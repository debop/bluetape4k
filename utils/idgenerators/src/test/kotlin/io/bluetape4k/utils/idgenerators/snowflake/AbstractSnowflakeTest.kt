package io.bluetape4k.utils.idgenerators.snowflake

import io.bluetape4k.collections.stream.asParallelStream
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import io.bluetape4k.utils.idgenerators.getMachineId
import io.bluetape4k.utils.idgenerators.parseAsLong
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterOrEqualTo
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeInRange
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

abstract class AbstractSnowflakeTest {

    companion object : KLogging() {
        private const val REPEAT_SIZE = 10
        private const val TEST_COUNT = MAX_SEQUENCE * 4
        private val TEST_LIST = List(TEST_COUNT) { it }
    }

    abstract val snowflake: Snowflake

    @Test
    fun `create machine id`() {
        val machineId = getMachineId(MAX_MACHINE_ID)
        machineId shouldBeInRange (0 until MAX_MACHINE_ID)
    }

    @RepeatedTest(10)
    fun `generate snowflake id`() {
        snowflake.nextId()

        val id1 = snowflake.nextId()
        val id2 = snowflake.nextId()
        val id3 = snowflake.nextId()

        id2 shouldBeGreaterThan id1
        id3 shouldBeGreaterThan id2

        listOf(id1, id2, id3).forEach {
            log.trace { "id=$it, ${snowflake.parse(it)}" }
        }
    }

    @RepeatedTest(10)
    fun `generate snowflake ids`() {
        val ids = snowflake.nextIds(TEST_COUNT).toList()
        verifyIds(ids)
    }

    @RepeatedTest(10)
    fun `generate snowflake as parallel`() {
        val ids = TEST_LIST.parallelStream().map { snowflake.nextId() }.sorted().toList()
        verifyIds(ids)
    }

    @RepeatedTest(10)
    fun `generate snowflake id as string`() {
        val id1 = snowflake.nextIdAsString()
        val id2 = snowflake.nextIdAsString()
        val id3 = snowflake.nextIdAsString()

        log.trace {
            """
            |
            |id1 = $id1
            |id2 = $id2
            |id3 = $id3
            """.trimMargin()
        }

        (id2 > id1).shouldBeTrue()
        (id3 > id2).shouldBeTrue()
    }

    @RepeatedTest(10)
    fun `generate snowflake ids as string`() {
        val ids = snowflake.nextIdsAsString(TEST_COUNT).toList()
        verifyIdsAsString(ids)
    }

    @RepeatedTest(10)
    fun `generate snowflake id as String as parallel`() {
        val ids = TEST_LIST.parallelStream().map { snowflake.nextIdAsString() }.sorted().toList()
        verifyIdsAsString(ids)
    }

    @Test
    fun `make and parse snowflake id`() {
        repeat(TEST_COUNT) {
            val id = snowflake.nextId()
            val parsedId = parseSnowflakeId(id)
            parsedId.value shouldBeEqualTo id
        }
    }

    @Test
    fun `make and parse snowflake id as string`() {
        repeat(TEST_COUNT) {
            val idString = snowflake.nextIdAsString()
            val parsedId = parseSnowflakeId(idString.parseAsLong(Character.MAX_RADIX))

            parsedId.valueAsString shouldBeEqualTo idString
        }
    }

    @RepeatedTest(10)
    fun `parse snowflake id`() {
        snowflake.nextId()  // for warmup

        val id1 = snowflake.nextId()
        val id2 = snowflake.nextId()

        val snowflakeId1 = snowflake.parse(id1)
        val snowflakeId2 = snowflake.parse(id2)

        Thread.sleep(1L)
        val id3 = snowflake.nextId()
        val snowflakeId3 = snowflake.parse(id3)

        snowflakeId2.timestamp shouldBeGreaterOrEqualTo snowflakeId1.timestamp
        snowflakeId3.timestamp shouldBeGreaterThan snowflakeId2.timestamp

        snowflakeId1.value shouldBeEqualTo id1
        snowflakeId2.value shouldBeEqualTo id2
        snowflakeId3.value shouldBeEqualTo id3
    }

    @RepeatedTest(10)
    fun `parse snowflake ids as sequence`() {
        val ids = snowflake.nextIds(TEST_COUNT).toList()
        val snowflakeIds = ids.map { snowflake.parse(it) }.toList()

        snowflakeIds.size shouldBeEqualTo ids.size
        snowflakeIds.all { ids.contains(it.value) }.shouldBeTrue()
    }

    @RepeatedTest(10)
    fun `parse snowflake ids as parallel`() {
        val ids = snowflake.nextIds(TEST_COUNT).toList()
        val snowflakeIds = ids.asParallelStream().map { snowflake.parse(it) }.toList()

        snowflakeIds.size shouldBeEqualTo ids.size
        snowflakeIds.all { ids.contains(it.value) }.shouldBeTrue()
    }

    @RepeatedTest(10)
    fun `parse snowflake id as string`() {
        val id1 = snowflake.nextIdAsString()
        val id2 = snowflake.nextIdAsString()
        Thread.sleep(1L)
        val id3 = snowflake.nextIdAsString()
        val id4 = snowflake.nextIdAsString()

        val snowflakeId1 = snowflake.parse(id1)
        val snowflakeId2 = snowflake.parse(id2)
        val snowflakeId3 = snowflake.parse(id3)
        val snowflakeId4 = snowflake.parse(id4)

        snowflakeId1.valueAsString shouldBeEqualTo id1
        snowflakeId2.valueAsString shouldBeEqualTo id2
        snowflakeId3.valueAsString shouldBeEqualTo id3
        snowflakeId4.valueAsString shouldBeEqualTo id4

        log.trace { "snowflakeId1=$snowflakeId1" }
        log.trace { "snowflakeId2=$snowflakeId2" }
        log.trace { "snowflakeId3=$snowflakeId3" }
        log.trace { "snowflakeId4=$snowflakeId4" }

        snowflakeId1.value shouldBeEqualTo id1.parseAsLong()

        snowflakeId2.timestamp shouldBeGreaterOrEqualTo snowflakeId1.timestamp
        snowflakeId3.timestamp shouldBeGreaterThan snowflakeId2.timestamp

        snowflakeId4.timestamp shouldBeGreaterOrEqualTo snowflakeId3.timestamp
        snowflakeId4.timestamp shouldBeGreaterThan snowflakeId2.timestamp
    }

    @RepeatedTest(10)
    fun `parse snowflake ids as String`() {
        val ids = snowflake.nextIdsAsString(TEST_COUNT).toList()
        val snowflakeIds = ids.map { snowflake.parse(it).value }.toList()

        snowflakeIds.forEachIndexed { index, snowflakeId ->
            snowflakeId shouldBeEqualTo ids[index].parseAsLong()
        }
        snowflakeIds.toSet().size shouldBeEqualTo ids.size
    }

    @RepeatedTest(10)
    fun `parse snowflake id as String as parallel`() {
        val ids = snowflake.nextIdsAsString(TEST_COUNT).toList()
        val snowflakeIds = ids.asParallelStream().map { snowflake.parse(it).value }.sorted().toList()

        snowflakeIds.forEachIndexed { index, snowflakeId ->
            snowflakeId shouldBeEqualTo ids[index].parseAsLong()
        }
        snowflakeIds.toSet().size shouldBeEqualTo ids.size
    }

    private fun verifyIds(ids: List<Long>) {
        val sorted = ids.sorted()
        sorted.forEachIndexed { index, id ->
            id shouldBeEqualTo ids[index]
        }
        ids.toSet().size shouldBeEqualTo ids.size
    }

    private fun verifyIdsAsString(ids: List<String>) {
        val sorted = ids.sorted()
        sorted.forEachIndexed { index, id ->
            id shouldBeEqualTo ids[index]
        }
        ids.toSet().size shouldBeEqualTo ids.size
    }
}
