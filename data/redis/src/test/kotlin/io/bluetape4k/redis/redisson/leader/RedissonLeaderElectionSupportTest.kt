package io.bluetape4k.redis.redisson.leader

import io.bluetape4k.concurrent.futureOf
import io.bluetape4k.junit5.concurrency.MultithreadingTester
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.redis.redisson.AbstractRedissonTest
import kotlinx.atomicfu.atomic
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class RedissonLeaderElectionSupportTest: AbstractRedissonTest() {

    companion object: KLogging()

    @Test
    fun `run action if leader`() {
        val lockName = randomName()
        val executor = Executors.newCachedThreadPool()
        val countDownLatch = CountDownLatch(2)

        executor.run {
            redissonClient.runIfLeader(lockName) {
                log.debug { "작업 1 을 시작합니다." }
                Thread.sleep(100)
                log.debug { "작업 1 을 종료합니다." }
                countDownLatch.countDown()
            }
        }
        executor.run {
            redissonClient.runIfLeader(lockName) {
                log.debug { "작업 2 을 시작합니다." }
                Thread.sleep(100)
                log.debug { "작업 2 을 종료합니다." }
                countDownLatch.countDown()
            }
        }

        countDownLatch.await()
        executor.shutdownNow()
    }

    @Test
    fun `run async action if leader`() {
        val lockName = randomName()
        val countDownLatch = CountDownLatch(2)

        val future1 = futureOf {
            redissonClient.runAsyncIfLeader(lockName) {
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
            redissonClient.runAsyncIfLeader(lockName) {
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

    @Test
    fun `run action if leader in multi threading`() {
        val lockName = randomName()

        val task1 = atomic(0)
        val task2 = atomic(0)

        MultithreadingTester()
            .numThreads(36)
            .roundsPerThread(4)
            .add {
                redissonClient.runIfLeader(lockName) {
                    log.debug { "작업 1 을 시작합니다. task1=${task1.value}" }
                    task1.incrementAndGet()
                    Thread.sleep(Random.nextLong(5, 10))
                    log.debug { "작업 1 을 종료합니다. task1=${task1.value}" }
                }
            }
            .add {
                redissonClient.runIfLeader(lockName) {
                    log.debug { "작업 2 을 시작합니다. task2=${task2.value}" }
                    task2.incrementAndGet()
                    Thread.sleep(Random.nextLong(5, 10))
                    log.debug { "작업 2 을 종료합니다. task2=${task2.value}" }
                }
            }
            .run()

        task1.value shouldBeEqualTo 36 * 2
        task2.value shouldBeEqualTo 36 * 2
    }

    @Test
    fun `run async action if leader in multi threading`() {
        val lockName = randomName()

        val task1 = atomic(0)
        val task2 = atomic(0)

        MultithreadingTester()
            .numThreads(36)
            .roundsPerThread(4)
            .add {
                redissonClient.runAsyncIfLeader(lockName) {
                    futureOf {
                        log.debug { "작업 1 을 시작합니다. task1=${task1.value}" }
                        task1.incrementAndGet()
                        Thread.sleep(Random.nextLong(5, 10))
                        log.debug { "작업 1 을 종료합니다. task1=${task1.value}" }
                        42
                    }
                }.join()
            }
            .add {
                redissonClient.runAsyncIfLeader(lockName) {
                    futureOf {
                        log.debug { "작업 2 을 시작합니다. task2=${task2.value}" }
                        task2.incrementAndGet()
                        Thread.sleep(Random.nextLong(5, 10))
                        log.debug { "작업 2 을 종료합니다. task2=${task2.value}" }
                        43
                    }
                }.join()
            }
            .run()

        task1.value shouldBeEqualTo 36 * 2
        task2.value shouldBeEqualTo 36 * 2
    }
}
