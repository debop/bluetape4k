package io.bluetape4k.utils.idgenerators.uuid

import io.bluetape4k.collections.eclipse.fastList
import io.bluetape4k.collections.eclipse.fastListOf
import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.collections.eclipse.toUnifiedSet
import io.bluetape4k.collections.stream.toFastList
import io.bluetape4k.junit5.concurrency.MultithreadingTester
import io.bluetape4k.junit5.coroutines.MultiJobTester
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.toLongArray
import io.bluetape4k.support.toUUID
import io.bluetape4k.utils.Runtimex
import io.bluetape4k.utils.idgenerators.hashids.Hashids
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.RepeatedTest
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.test.assertTrue

class TimebasedUuidGeneratorTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
        private val TEST_COUNT = 512 * Runtime.getRuntime().availableProcessors()
        private val TEST_LIST = fastList(TEST_COUNT) { it }
    }

    private val uuidGenerator = TimebasedUuidGenerator()

    @RepeatedTest(REPEAT_SIZE)
    fun `generate timebased uuid`() {
        val u1 = uuidGenerator.nextUUID()
        val u2 = uuidGenerator.nextUUID()
        val u3 = uuidGenerator.nextUUID()

        fastListOf(u1, u2, u3).forEach {
            log.debug { "uuid=$it" }
        }

        assertTrue { u2 > u1 }
        assertTrue { u3 > u2 }

        // u1.version() shouldBeEqualTo 6   // Time based
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `generate timebased uuid with size`() {

        val uuids = uuidGenerator.nextUUIDs(TEST_COUNT).toFastList()
        val sorted = uuids.sorted().toList()

        sorted.forEachIndexed { index, uuid ->
            uuid shouldBeEqualTo sorted[index]
        }

        uuids.toUnifiedSet().size shouldBeEqualTo uuids.size
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `generate timebased uuids as parallel`() {
        val uuids = TEST_LIST.parallelStream()
            .map { uuidGenerator.nextUUID() }
            .toFastList()
            .sortThis()

        // 중복 발행은 없어야 한다
        uuids.toUnifiedSet().size shouldBeEqualTo uuids.size
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `generate timebased uuids in multi thread`() {
        val idMap = ConcurrentHashMap<UUID, Int>()
        MultithreadingTester()
            .numThreads(2 * Runtimex.availableProcessors)
            .roundsPerThread(TEST_COUNT)
            .add {
                val id = uuidGenerator.nextUUID()
                idMap.putIfAbsent(id, 1).shouldBeNull()
            }
            .run()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `generate timebased uuids in multi job`() = runTest {
        val idMap = ConcurrentHashMap<UUID, Int>()
        MultiJobTester()
            .numJobs(2 * Runtimex.availableProcessors)
            .roundsPerJob(TEST_COUNT)
            .add {
                val id = uuidGenerator.nextUUID()
                idMap.putIfAbsent(id, 1).shouldBeNull()
            }
            .run()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `convert timebaed uuids to hashids`() {
        val hashids = Hashids()

        val uuids = TEST_LIST.parallelStream().map { uuidGenerator.nextUUID() }.toFastList()
        val encodeds = uuids.map { hashids.encode(*it.toLongArray()) }

        val decodeds = encodeds.map { hashids.decode(it).toUUID() }
        decodeds shouldContainSame uuids
    }
}
