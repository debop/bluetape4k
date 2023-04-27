package io.bluetape4k.data.redis.redisson.leader.coroutines

import io.bluetape4k.data.redis.redisson.AbstractRedissonTest
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test

class RedissonCoLeaderElectionTest: AbstractRedissonTest() {

    companion object: KLogging()

    @Test
    fun `run suspend action if leader`() = runSuspendWithIO {
        val lockName = randomName()
        val leaderElection = RedissonCoLeaderElection(redissonClient)

        coroutineScope {
            launch {
                leaderElection.runIfLeader(lockName) {
                    log.debug { "작업 1 을 시작합니다." }
                    delay(100)
                    log.debug { "작업 1 을 종료합니다." }
                }
            }
            launch {
                leaderElection.runIfLeader(lockName) {
                    log.debug { "작업 2 을 시작합니다." }
                    delay(100)
                    log.debug { "작업 2 을 종료합니다." }
                }
            }
        }
    }

}
