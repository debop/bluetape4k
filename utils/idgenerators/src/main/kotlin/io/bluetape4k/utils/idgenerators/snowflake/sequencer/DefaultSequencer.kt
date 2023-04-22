package io.bluetape4k.utils.idgenerators.snowflake.sequencer

import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.idgenerators.getMachineId
import io.bluetape4k.utils.idgenerators.snowflake.MAX_MACHINE_ID
import io.bluetape4k.utils.idgenerators.snowflake.MAX_SEQUENCE
import io.bluetape4k.utils.idgenerators.snowflake.SnowflakeId
import java.util.concurrent.atomic.LongAdder
import java.util.concurrent.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlin.math.absoluteValue

internal class DefaultSequencer(machineId: Int = getMachineId(MAX_MACHINE_ID)): Sequencer {

    companion object: KLogging()

    override val machineId: Int = machineId.absoluteValue % MAX_MACHINE_ID

    @Volatile
    private var currentTimestamp: Long = -1L

    @Volatile
    private var lastTimestamp: Long = -1L
    private val sequencer = LongAdder()
    private val lock = ReentrantLock()

    /**
     * 현재의 Timestamp와 순 증가되는 sequence 값을 제공합니다.
     * 같은 Timestamp 에서 발급할 수 있는 sequence 값이 []MAX_SEQUENCE] 값을 초과하면,
     * Timestamp를 증가 시키고, sequence를 0으로 reset 합니다.
     *
     * MAX_SEQUENCE 값보더 더 많은 sequence를 생성하려면 최대 1 msec가 소요됩니다.
     */
    override fun nextSequence(): SnowflakeId {
        lock.withLock {
            updateState()
            return SnowflakeId(lastTimestamp, machineId, sequencer.toInt())
        }
    }

    override fun nextSequences(size: Int): List<SnowflakeId> {
        lock.withLock {
            return List(size) {
                updateState()
                SnowflakeId(lastTimestamp, machineId, sequencer.toInt())
            }
        }
    }

    private fun updateState() {
        currentTimestamp = System.currentTimeMillis()

        if (currentTimestamp == lastTimestamp) {
            sequencer.increment()
            // sequence 가 MAX_SEQUENCE 값보다 증가하면, 다음 milliseconds까지 기다립니다.
            if (sequencer.toLong() >= MAX_SEQUENCE) {
                while (currentTimestamp == lastTimestamp) {
                    currentTimestamp = System.currentTimeMillis()
                }
                sequencer.reset()
                lastTimestamp = currentTimestamp
            }
        } else {
            sequencer.reset()
            lastTimestamp = currentTimestamp
        }
    }
}
