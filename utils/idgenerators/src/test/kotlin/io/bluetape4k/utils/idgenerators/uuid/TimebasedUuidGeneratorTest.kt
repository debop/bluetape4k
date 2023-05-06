package io.bluetape4k.utils.idgenerators.uuid

import io.bluetape4k.junit5.concurrency.MultithreadingTester
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.toLongArray
import io.bluetape4k.support.toUUID
import io.bluetape4k.utils.idgenerators.hashids.Hashids
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.RepeatedTest
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class TimebasedUuidGeneratorTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 10
        private val TEST_COUNT = 1024 * Runtime.getRuntime().availableProcessors()
        private val TEST_LIST = List(TEST_COUNT) { it }
    }

    private val uuidGenerator = TimebasedUuidGenerator()

    @RepeatedTest(REPEAT_SIZE)
    fun `generate timebased uuid`() {
        val u1 = uuidGenerator.nextUUID()
        val u2 = uuidGenerator.nextUUID()
        val u3 = uuidGenerator.nextUUID()

        listOf(u1, u2, u3).forEach {
            log.debug { "uuid=$it" }
        }

        Assertions.assertTrue { u2 > u1 }
        Assertions.assertTrue { u3 > u2 }

        // u1.version() shouldBeEqualTo 6   // Time based
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `generate timebased uuid with size`() {

        val uuids = uuidGenerator.nextUUIDs(TEST_COUNT).toList()
        val sorted = uuids.sorted().toList()

        sorted.forEachIndexed { index, uuid ->
            uuid shouldBeEqualTo sorted[index]
        }

        uuids.toSet().size shouldBeEqualTo uuids.size
    }


    @RepeatedTest(REPEAT_SIZE)
    fun `generate timebased uuids as parallel`() {
        val uuids = TEST_LIST.parallelStream().map { uuidGenerator.nextUUID() }.toList().sorted()

        // 중복 발행은 없어야 한다
        uuids.toSet().size shouldBeEqualTo uuids.size
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `generate timebased uuids in multithread`() {
        val idMap = ConcurrentHashMap<UUID, Int>()
        MultithreadingTester().numThreads(100).roundsPerThread(TEST_COUNT)
            .add {
                val id = uuidGenerator.nextUUID()
                idMap.putIfAbsent(id, 1).shouldBeNull()
            }
            .run()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `convert timebaed uuids to hashids`() {
        val hashids = Hashids()

        val uuids = TEST_LIST.parallelStream().map { uuidGenerator.nextUUID() }.toList()
        val encodeds = uuids.map { hashids.encode(*it.toLongArray()) }

        val decodeds = encodeds.map { hashids.decode(it).toUUID() }
        decodeds shouldContainSame uuids
    }
}
