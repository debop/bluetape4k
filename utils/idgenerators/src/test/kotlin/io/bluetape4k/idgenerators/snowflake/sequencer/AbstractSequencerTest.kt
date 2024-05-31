package io.bluetape4k.idgenerators.snowflake.sequencer

import io.bluetape4k.idgenerators.snowflake.MAX_MACHINE_ID
import io.bluetape4k.idgenerators.snowflake.MAX_SEQUENCE
import io.bluetape4k.idgenerators.snowflake.SnowflakeId
import io.bluetape4k.junit5.concurrency.MultithreadingTester
import io.bluetape4k.junit5.concurrency.VirtualthreadTester
import io.bluetape4k.junit5.coroutines.MultiJobTester
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeLessThan
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.IntStream
import kotlin.math.absoluteValue

abstract class AbstractSequencerTest {

    companion object: KLogging() {
        private const val TEST_SIZE: Int = MAX_SEQUENCE * 2
        private const val REPEAT_SIZE = 3
    }

    protected abstract val sequencer: Sequencer

    private val comparator = Comparator { id1: SnowflakeId, id2: SnowflakeId ->
        var comparison = (id1.timestamp - id2.timestamp).toInt()
        if (comparison == 0) {
            comparison = id1.machineId - id2.machineId
        }
        comparison
    }

    @BeforeAll
    fun setup() {
        // Warm up sequencer
        IntStream.range(0, TEST_SIZE)
            .parallel()
            .mapToObj { sequencer.nextSequence() }
            .toList()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `generate sequence`() {
        val ids = List(TEST_SIZE) { sequencer.nextSequence() }

        ids shouldHaveSize TEST_SIZE
        ids.distinct() shouldBeEqualTo ids
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `generate sequence as parallel`() {
        val ids = IntStream.range(0, TEST_SIZE)
            .parallel()
            .mapToObj { sequencer.nextSequence() }
            .toList()

        //ids.count { it.machineId > 0 } shouldBeGreaterThan 0
        ids.distinct().size shouldBeEqualTo ids.size

        val resetIds = ids.filter { it.sequence == 0 }.sortedWith(comparator)
        resetIds.size shouldBeLessThan ids.size
    }

    @ParameterizedTest(name = "create Snowflake instance with machineId {0}")
    @ValueSource(ints = [-1000, -100, -1, 0, 1, 100, 1000])
    fun `create Snowflake instance with invalid machineId`(machineId: Int) {
        val sequencer = DefaultSequencer(machineId)
        sequencer.machineId shouldBeEqualTo (machineId.absoluteValue % MAX_MACHINE_ID)
    }

    @Test
    fun `generate sequence in multi-threading`() {
        val idMap = ConcurrentHashMap<Long, Int>()

        MultithreadingTester()
            .numThreads(100)
            .roundsPerThread(MAX_SEQUENCE * 2)
            .add {
                val id = sequencer.nextSequence()
                idMap.putIfAbsent(id.value, 1).shouldBeNull()
            }
            .run()
    }

    @Disabled("수행이 오래 걸린다")
    @Test
    fun `generate sequence in virtual threads`() {
        val idMap = ConcurrentHashMap<Long, Int>()

        VirtualthreadTester()
            .numThreads(100)
            .roundsPerThread(MAX_SEQUENCE * 2)
            .add {
                val id = sequencer.nextSequence()
                idMap.putIfAbsent(id.value, 1).shouldBeNull()
            }
            .run()
    }

    @Test
    fun `generate sequence in multi job`() = runTest {
        val idMap = ConcurrentHashMap<Long, Int>()

        MultiJobTester()
            .numJobs(100)
            .roundsPerJob(MAX_SEQUENCE * 2)
            .add {
                val id = sequencer.nextSequence()
                idMap.putIfAbsent(id.value, 1).shouldBeNull()
            }
            .run()
    }
}
