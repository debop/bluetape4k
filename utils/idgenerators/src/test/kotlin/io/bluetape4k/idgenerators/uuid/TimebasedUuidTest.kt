package io.bluetape4k.idgenerators.uuid

import io.bluetape4k.idgenerators.IdGenerator
import io.bluetape4k.junit5.concurrency.MultithreadingTester
import io.bluetape4k.junit5.concurrency.VirtualthreadTester
import io.bluetape4k.junit5.coroutines.MultiJobTester
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.utils.Runtimex
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import java.util.*
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractTimebasedUuidTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
        private val TEST_COUNT = 512 * Runtime.getRuntime().availableProcessors()
        private val TEST_LIST = List(TEST_COUNT) { it }
    }

    abstract val uuidGenerator: IdGenerator<UUID>

    @RepeatedTest(REPEAT_SIZE)
    fun `generate uuid`() {
        val uuid1 = uuidGenerator.nextId()
        val uuid2 = uuidGenerator.nextId()

        log.debug { "uuid1=$uuid1" }
        log.debug { "uuid2=$uuid2" }
        uuid2 shouldNotBeEqualTo uuid1
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `generate uuid as string`() {
        val uuid1 = uuidGenerator.nextIdAsString()
        val uuid2 = uuidGenerator.nextIdAsString()

        log.debug { "uuid1=$uuid1" }
        log.debug { "uuid2=$uuid2" }
        uuid2 shouldNotBeEqualTo uuid1
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `generate multiple uuids`() {
        val uuids = uuidGenerator.nextIds(10).toList()

        uuids.distinct() shouldBeEqualTo uuids
        uuids.sorted() shouldBeEqualTo uuids
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `generate multiple uuids as string`() {
        val uuids = uuidGenerator.nextIdsAsString(10).toList()

        uuids.distinct() shouldBeEqualTo uuids
        uuids.sorted() shouldBeEqualTo uuids
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `generate timebased uuids in multi thread`() {
        val idMap = ConcurrentHashMap<UUID, Int>()
        MultithreadingTester()
            .numThreads(2 * Runtimex.availableProcessors)
            .roundsPerThread(TEST_COUNT)
            .add {
                val id = uuidGenerator.nextId()
                idMap.putIfAbsent(id, 1).shouldBeNull()
            }
            .run()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `generate timebased uuids in virtual threads`() {
        val idMap = ConcurrentHashMap<UUID, Int>()
        VirtualthreadTester()
            .numThreads(2 * Runtimex.availableProcessors)
            .roundsPerThread(TEST_COUNT)
            .add {
                val id = uuidGenerator.nextId()
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
                val id = uuidGenerator.nextId()
                idMap.putIfAbsent(id, 1).shouldBeNull()
            }
            .run()
    }
}

class DefaultTimebasedUuidTest: AbstractTimebasedUuidTest() {
    override val uuidGenerator: IdGenerator<UUID> = TimebasedUuid.Default
}

class ReorderedTimebasedUuidTest: AbstractTimebasedUuidTest() {
    override val uuidGenerator: IdGenerator<UUID> = TimebasedUuid.Reordered
}

class EpochTimebasedUuidTest: AbstractTimebasedUuidTest() {
    override val uuidGenerator: IdGenerator<UUID> = TimebasedUuid.Epoch
}
