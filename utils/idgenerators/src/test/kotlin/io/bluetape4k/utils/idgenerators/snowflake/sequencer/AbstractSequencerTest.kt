package io.bluetape4k.utils.idgenerators.snowflake.sequencer

import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.idgenerators.snowflake.MAX_MACHINE_ID
import io.bluetape4k.utils.idgenerators.snowflake.MAX_SEQUENCE
import io.bluetape4k.utils.idgenerators.snowflake.SnowflakeId
import java.util.stream.IntStream
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeLessThan
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.math.absoluteValue

abstract class AbstractSequencerTest {

    companion object : KLogging() {
        private const val TEST_SIZE: Int = MAX_SEQUENCE * 4
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

    @RepeatedTest(10)
    fun `generate sequence`() {
        val ids = List(TEST_SIZE) { sequencer.nextSequence() }

        ids shouldHaveSize TEST_SIZE
        ids.distinct() shouldBeEqualTo ids
//        ids.forEach {
//            log.trace { it }
//        }
    }

    @RepeatedTest(10)
    fun `generate sequence as parallel`() {
        val ids = IntStream.range(0, TEST_SIZE)
            .parallel()
            .mapToObj { sequencer.nextSequence() }
            .toList()

//        ids.forEach {
//            log.trace { it }
//        }

        //ids.count { it.machineId > 0 } shouldBeGreaterThan 0
        ids.distinct().size shouldBeEqualTo ids.size

        val resetIds = ids.filter { it.sequence == 0 }.sortedWith(comparator)
        resetIds.size shouldBeLessThan ids.size
    }

    @ParameterizedTest
    @ValueSource(ints = [-1000, -100, -1, 0, 1, 100, 1000])
    fun `create Snowflake instance with invalid machineId`(machineId: Int) {
        val sequencer = DefaultSequencer(machineId)
        sequencer.machineId shouldBeEqualTo (machineId.absoluteValue % MAX_MACHINE_ID)
    }
}
