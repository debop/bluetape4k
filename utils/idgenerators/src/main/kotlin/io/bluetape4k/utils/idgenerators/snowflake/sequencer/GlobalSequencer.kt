package io.bluetape4k.utils.idgenerators.snowflake.sequencer

import io.bluetape4k.collections.eclipse.FastList
import io.bluetape4k.utils.idgenerators.snowflake.MAX_MACHINE_ID
import io.bluetape4k.utils.idgenerators.snowflake.MAX_SEQUENCE
import io.bluetape4k.utils.idgenerators.snowflake.SnowflakeId
import java.util.concurrent.locks.ReentrantLock
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.withLock

/**
 * MachineId 구분 없이 Sequence를 생성합니다.
 *
 * 같은 Timestamp 에서 발급할 수 있는 sequence 값이 [MAX_SEQUENCE] 값을 초과하면,
 * Machine Id를 증가시키고, MachineId 가 [MAX_MACHINE_ID] 보다 커지면
 * 시스템 시각이 lastTimestamp보다 커지기를 기다렸다가 machineId와 sequence를 reset한 값을 제공합니다.
 *
 * MAX_MACHINE_ID * MAX_SEQUENCE 값보더 더 많은 sequence를 생성하려면 최대 1 msec가 소요됩니다.
 */
class GlobalSequencer: Sequencer {

    @Volatile
    private var currentTimestamp: Long = -1L

    @Volatile
    private var lastTimestamp: Long = -1L

    private val machineIdSequencer = atomic(0)
    private val sequencer = atomic(0)

    private val lock = ReentrantLock()

    override var machineId: Int by machineIdSequencer

    /**
     * 현재의 Timestamp와 순 증가되는 sequence 값을 제공합니다.
     *
     * 같은 Timestamp 에서 발급할 수 있는 sequence 값이 [MAX_SEQUENCE] 값을 초과하면,
     * Machine Id를 증가시키고, MachineId 가 [MAX_MACHINE_ID] 보다 커지면
     * 시스템 시각이 lastTimestamp보다 커지기를 기다렸다가 machineId와 sequence를 reset한 값을 제공합니다.
     *
     * MAX_MACHINE_ID * MAX_SEQUENCE 값보더 더 많은 sequence를 생성하려면 최대 1 msec가 소요됩니다.
     */
    override fun nextSequence(): SnowflakeId {
        lock.withLock {
            updateState()
            return SnowflakeId(lastTimestamp, machineId, sequencer.value)
        }
    }

    override fun nextSequences(size: Int): List<SnowflakeId> {
        lock.withLock {
            return FastList(size) {
                updateState()
                SnowflakeId(lastTimestamp, machineId, sequencer.value)
            }
        }
    }

    private fun updateState() {
        currentTimestamp = System.currentTimeMillis()

        if (currentTimestamp == lastTimestamp) {
            sequencer.incrementAndGet()
            if (sequencer.value >= MAX_SEQUENCE) {
                machineIdSequencer.incrementAndGet()

                // sequence 가 MAX_SEQUENCE 값보다 증가하면, 다음 milliseconds까지 기다립니다.
                if (machineId >= MAX_MACHINE_ID) {
                    while (currentTimestamp == lastTimestamp) {
                        currentTimestamp = System.currentTimeMillis()
                    }
                    machineIdSequencer.value = 0
                    lastTimestamp = currentTimestamp
                }
                sequencer.value = 0
            }
        } else {
            // Reset sequence and machine id
            sequencer.value = 0
            machineId = 0
            lastTimestamp = currentTimestamp
        }
    }
}
