package io.bluetape4k.examples.redisson.coroutines.locks

import io.bluetape4k.data.redis.redisson.coroutines.awaitSuspending
import io.bluetape4k.data.redis.redisson.coroutines.getLockId
import io.bluetape4k.examples.redisson.coroutines.AbstractRedissonCoroutineTest
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeGreaterOrEqualTo
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test

class LockExamples: AbstractRedissonCoroutineTest() {

    companion object: KLogging()

    @Test
    fun `lock example`() = runSuspendWithIO {
        val lockName = randomName()

        val lock1 = redisson.getLock(lockName)
        val lockId1 = redisson.getLockId(lockName)

        log.debug { "Lock1 lock in 2 seconds." }
        log.debug { "Lock1 lock: current coroutineId=$lockId1, threadId=${Thread.currentThread().id}" }
        lock1.lockAsync(60, TimeUnit.SECONDS, lockId1).awaitSuspending()
        lock1.isLockedAsync.awaitSuspending().shouldBeTrue()

        // 다른 Coroutine context 에서 Lock 잡고, 풀기
        val job = scope.launch(exceptionHandler) {
            log.debug { "Lock2 lock" }
            val lock2 = redisson.getLock(lockName)
            val lockId2 = redisson.getLockId(lockName)
            // 이미 lock이 잡혀 있다.
            lock2.isLockedAsync.awaitSuspending().shouldBeTrue()
            // lock1 과 다른 currentCoroutineId 를 가지므로 실패한다.
            lock2.tryLockAsync(lockId2).awaitSuspending().shouldBeFalse()
            lock2.isLockedAsync.awaitSuspending().shouldBeTrue()

            delay(100)

            // lock1 에서 이미 lock 이 걸렸고, lock2는 소유권이 없으므로 lock2로는 unlock 할 수 없다
            log.debug { "Lock2 unlock: current coroutineId=$lockId2, threadId=${Thread.currentThread().id}" }
            runCatching {
                lock2.unlockAsync().awaitSuspending()
            }
            lock2.isLockedAsync.awaitSuspending().shouldBeTrue()
        }
        delay(1000)
        job.join()
        delay(10)

        log.debug { "lock1.isLocked=${lock1.isLocked}" }
        lock1.unlockAsync(lockId1).awaitSuspending()
        lock1.isLockedAsync.awaitSuspending().shouldBeFalse()
    }

    @Test
    fun `tryLock with expiration`() = runSuspendWithIO {
        val lockName = randomName()

        val lock = redisson.getLock(lockName)
        val lockId = redisson.getLockId(lockName)

        log.debug { "Main Thread에서 tryLock 시도" }
        val acquired1 = lock.tryLockAsync(1, 60, TimeUnit.SECONDS, lockId).awaitSuspending()
        acquired1.shouldBeTrue()
        lock.isLockedAsync.awaitSuspending().shouldBeTrue()

        val ttl1 = lock.remainTimeToLiveAsync().awaitSuspending()
        log.debug { "TTL1: $ttl1" }
        ttl1 shouldBeGreaterThan 0L

        val job = scope.launch(exceptionHandler) {
            log.debug { "다른 Coroutine scope에서 기존 lock에 tryLock 시도 -> 소유권이 다르므로 실패한다" }
            val lockId2 = redisson.getLockId(lockName)
            lock.tryLockAsync(1, 60, TimeUnit.SECONDS, lockId2).awaitSuspending().shouldBeFalse()
        }
        delay(5)
        job.join()

        val prevTtl = lock.remainTimeToLiveAsync().awaitSuspending()

        // 같은 Thread 에서 기존 lock이 걸려 있는데, 또 lock을 걸면 TTL이 갱신된다 (ttl3 >= prevTtl)
        lock.tryLockAsync(1, 60, TimeUnit.SECONDS, lockId).awaitSuspending().shouldBeTrue()

        val ttl3 = lock.remainTimeToLiveAsync().awaitSuspending()
        log.debug { "TTL3: $ttl3, PrevTTL: $prevTtl" }
        ttl3 shouldBeGreaterOrEqualTo prevTtl
    }
}
