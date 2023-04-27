package io.bluetape4k.data.redis.redisson.leader

import io.bluetape4k.concurrent.futureOf
import io.bluetape4k.data.redis.redisson.AbstractRedissonTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class RedissonLeaderElectionTest: AbstractRedissonTest() {

    companion object: KLogging()

    @Test
    fun `run action if leader`() {
        val lockName = randomName()
        val leaderElection = RedissonLeaderElection(redissonClient)

        val executor = Executors.newCachedThreadPool()
        try {
            val countDownLatch = CountDownLatch(2)

            executor.run {
                leaderElection.runIfLeader(lockName) {
                    log.debug { "작업 1 을 시작합니다." }
                    Thread.sleep(100)
                    log.debug { "작업 1 을 종료합니다." }
                    countDownLatch.countDown()
                }
            }
            executor.run {
                leaderElection.runIfLeader(lockName) {
                    log.debug { "작업 2 을 시작합니다." }
                    Thread.sleep(100)
                    log.debug { "작업 2 을 종료합니다." }
                    countDownLatch.countDown()
                }
            }

            countDownLatch.await()
        } finally {
            executor.shutdownNow()
        }
    }

    @Test
    fun `run async action if leader`() {
        val lockName = randomName()
        val leaderElection = RedissonLeaderElection(redissonClient)
        val countDownLatch = CountDownLatch(2)

        val future1 = futureOf {
            leaderElection.runAsyncIfLeader(lockName) {
                futureOf {
                    log.debug { "작업 1 을 시작합니다." }
                    Thread.sleep(100)
                    log.debug { "작업 1 을 종료합니다." }
                    Thread.sleep(10)
                    countDownLatch.countDown()
                    42
                }
            }.join()
        }
        val future2 = futureOf {
            leaderElection.runAsyncIfLeader(lockName) {
                futureOf {
                    log.debug { "작업 2 을 시작합니다." }
                    Thread.sleep(100)
                    log.debug { "작업 2 을 종료합니다." }
                    Thread.sleep(10)
                    countDownLatch.countDown()
                    43
                }
            }.join()
        }
        countDownLatch.await(5, TimeUnit.SECONDS)
        future1.get() shouldBeEqualTo 42
        future2.get() shouldBeEqualTo 43
    }
}
