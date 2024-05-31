package io.bluetape4k.concurrent.virtualthread


import io.bluetape4k.junit5.coroutines.MultiJobTester
import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class VirtualThreadDispatcherTest {

    companion object: KLogging() {
        private const val TASK_SIZE = 1000
    }

    @Test
    fun `Virtual Thread Dispatcher를 이용하여 비동기 작업하기`() = runSuspendTest {
        val elapsedTime = measureTimeMillis {
            val tasks = List(TASK_SIZE) {
                async(Dispatchers.VT) {
                    delay(1000)
                    log.debug { "Task $it is done" }
                }
            }
            tasks.awaitAll()
        }
        log.debug { "Elapsed time: $elapsedTime ms" }
    }

    @Test
    fun `Virtual Thread Dispatcher를 이용하여 Job 실행하기`() = runSuspendTest(Dispatchers.newVT) {
        val elapsedTime = measureTimeMillis {
            val jobs = List(TASK_SIZE) {
                launch {
                    delay(1000)
                    log.debug { "Job $it is done" }
                }
            }
            jobs.joinAll()
        }
        log.debug { "Elapsed time: $elapsedTime ms" }
    }

    @Test
    fun `withContext with Dispatchers VT`() = runSuspendTest(Dispatchers.VT) {
        val elapsedTime = measureTimeMillis {
            val jobs = List(TASK_SIZE) {
                launch {
                    withContext(Dispatchers.VT) {
                        sleep(1000)
                        log.debug { "Job $it is done" }
                    }
                }
            }
            jobs.joinAll()
        }
        log.debug { "Elapsed time: $elapsedTime ms" }
    }

    @Test
    fun `multi job with virtual thread dispatcher`() = runSuspendTest(Dispatchers.VT) {
        val jobNumber = atomic(0)

        // 1초씩 delay 하는 1000개의 작업을 수행 시 거의 1초에 완료된다 (Virtual Thread)
        MultiJobTester()
            .numJobs(1000)
            .roundsPerJob(1)
            .add {
                sleep(1000)
                log.debug { "Job[${jobNumber.incrementAndGet()}] is done" }
            }
            .run()
    }

    @Test
    fun `multi job with default dispatcher`() = runSuspendTest(Dispatchers.Default) {
        val jobNumber = atomic(0)

        // 1초씩 delay 하는 1000개의 작업을 수행 시 거의 1초에 완료된다 (Default Dispatcher)
        MultiJobTester()
            .numJobs(1000)
            .roundsPerJob(1)
            .add {
                sleep(1000)
                log.debug { "Job[${jobNumber.incrementAndGet()}] is done" }
            }
            .run()
    }

    private fun sleep(delayTime: Long) {
        Thread.sleep(delayTime)
    }

    private suspend fun sleepSuspending(delayTime: Long) {
        delay(delayTime)
    }
}
