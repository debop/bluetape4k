package io.bluetape4k.data.redis.redisson.leader.coroutines

import io.bluetape4k.core.requireNotBlank
import io.bluetape4k.data.redis.redisson.coroutines.awaitSuspending
import io.bluetape4k.data.redis.redisson.leader.LeaderElectionOptions
import io.bluetape4k.kotlinx.coroutines.support.currentCoroutineId
import io.bluetape4k.leader.CoLeaderElection
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.warn
import io.bluetape4k.support.uninitialized
import kotlinx.coroutines.coroutineScope
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import java.time.LocalDate
import java.util.concurrent.TimeUnit

/**
 * 여러 Process, Thread에서 같은 작업이 동시, 무작위로 실행되는 것을 방지하기 위해
 * Redisson Lock을 이용하여 Leader를 선출되면 독점적으로 작업할 수 있도록 합니다.
 */
class CoRedissonLeaderElection(
    private val redissonClient: RedissonClient,
    private val options: LeaderElectionOptions,
) : CoLeaderElection {

    companion object : KLogging() {

        private fun getCoroutineSequenceName(): String {
            val today = LocalDate.now()
            val todaySequence = "${today.year}:${today.monthValue}:${today.dayOfMonth}"
            return "bluetape4k:leader:coroutineId:$todaySequence"
        }

        operator fun invoke(
            redissonClient: RedissonClient,
            options: LeaderElectionOptions = LeaderElectionOptions.Default
        ): CoRedissonLeaderElection {
            return CoRedissonLeaderElection(redissonClient, options)
        }
    }

    private val waitTimeMills = options.waitTime.toMillis()
    private val leaseTimeMills = options.leaseTime.toMillis()

    /**
     * Redisson Lock을 이용하여, 리더로 선출되면 [action]을 수행하고, 그렇지 않다면 수행하지 않습니다.
     *
     * @param lockName lock name - lock 획득에 성공하면 leader로 승격되는 것이다.
     * @param action leader 로 승격되면 수행할 코드 블럭
     * @return 작업 결과
     */
    override suspend fun <T> runIfLeader(lockName: String, action: suspend () -> T): T = coroutineScope {
        lockName.requireNotBlank("lockName")

        val lock: RLock = redissonClient.getLock(lockName)
        var result: T = uninitialized()

        try {
            log.debug { "Leader 승격을 요청합니다 ..." }
            // Thread Id 기반으로 Lock 을 걸게 되므로, Coroutines 환경에서는 사용할 수 없다.
            // 고유의 Id 값을 제공해야 하므로 [RAtomicLong] 을 사용한다.
            val corotuineSequence = redissonClient.getAtomicLong(getCoroutineSequenceName())
            val lockCoroutineId = runCatching { corotuineSequence.andIncrement }.getOrElse { currentCoroutineId }
            val acquired = lock.tryLockAsync(waitTimeMills, leaseTimeMills, TimeUnit.MILLISECONDS, lockCoroutineId)
                .awaitSuspending()
            if (acquired) {
                log.debug { "Leader로 승격되어 작업을 수행합니다. lock=$lockName, coroutineId=$lockCoroutineId" }
                try {
                    result = action()
                } finally {
                    if (lock.isHeldByThread(lockCoroutineId)) {
                        runCatching {
                            lock.unlockAsync(lockCoroutineId).awaitSuspending()
                            log.debug { "작업이 완료되어 Leader 권한을 반납했습니다. lock=$lockName, coroutineId=$lockCoroutineId" }
                        }
                    }
                }
            }
        } catch (e: InterruptedException) {
            log.warn(e) { "Interrupt to run action as leader. lockName=$lockName" }
        }
        result
    }
}
