package io.bluetape4k.examples.redisson.coroutines.locks


import io.bluetape4k.data.redis.redisson.coroutines.awaitSuspending
import io.bluetape4k.data.redis.redisson.coroutines.getLockId
import io.bluetape4k.examples.redisson.coroutines.AbstractRedissonCoroutineTest
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test


/**
 * Fair Lock
 *
 * Redis based distributed reentrant fair Lock object for Java implements Lock interface.
 * Fair lock guarantees that threads will acquire it in is same order they requested it.
 * All waiting threads are queued and if some thread has died then Redisson waits its return for 5 seconds.
 * For example, if 5 threads are died for some reason then delay will be 25 seconds.
 *
 * 참고: [FairLock](https://github.com/redisson/redisson/wiki/8.-distributed-locks-and-synchronizers#82-fair-lock)
 */
class FairLockExamples: AbstractRedissonCoroutineTest() {

    companion object: KLogging()

    @Test
    fun `acquire fair lock`() = runSuspendWithIO {
        val lock = redisson.getFairLock(randomName())
        val size = 10

        val jobs = List(size) {
            scope.launch {
                // 여러 Thread 가 lock을 요청하면, 요청 순서대로 lock 을 제공하는 것을 보장합니다.
                // 나머지 요청은 최대 5초간 대기하다가 요청 중단된다
                val lockId = redisson.getLockId(lock.name)
                log.trace { "lockId=$lockId" }
                lock.tryLockAsync(5, 10, TimeUnit.SECONDS, lockId).awaitSuspending()
                delay(10)
                lock.unlockAsync(lockId).awaitSuspending()
            }
        }
        jobs.joinAll()
    }
}
