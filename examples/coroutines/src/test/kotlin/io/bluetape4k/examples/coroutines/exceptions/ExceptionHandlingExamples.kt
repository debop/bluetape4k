package io.bluetape4k.examples.coroutines.exceptions

import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.error
import io.bluetape4k.logging.info
import io.bluetape4k.logging.warn
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.fail
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.coroutines.cancellation.CancellationException

class ExceptionHandlingExamples {

    companion object: KLogging()

    @Disabled("예외가 발생하는 launch 구역은 try/catch를 추가해야 합니다.")
    @Test
    fun `예외처리가 없는 경우에 복수의 Job을 실행하는 방식`() = runTest {
        coroutineScope {
            launch {
                launch {
                    delay(1000L)
                    throw RuntimeException("Boom!")
                }
                launch {
                    delay(2000L)
                    fail("이 코드까지 실행되면 안됩니다.")
                }
                launch {
                    delay(500)
                    log.info { "이 코드는 실행되어야 합니다." }
                }
            }

            launch {
                delay(2000)
                fail("이 코드까지 실행되면 안됩니다.")
            }
        }
    }

    @Disabled("외부에 try catch를 적용해도 예외를 잡지 못합니다")
    @Test
    fun `외부에 try catch를 적용해도 예외를 잡지 못합니다`() = runTest {
        try {
            launch {
                delay(1000L)
                throw RuntimeException("Boom!")
            }
        } catch (e: Throwable) {
            log.warn(e) { "예외를 catch하지 못했습니다" }
        }

        launch {
            delay(2000L)
            fail("이 코드는 실행되면 안됩니다")
        }
    }

    @Test
    fun `내부에 try catch를 적용해야 예외를 잡아냅니다`() = runTest {
        var capturedException: Throwable? = null

        coroutineScope {

            launch {
                try {
                    delay(1000L)
                    throw RuntimeException("Boom!")
                } catch (e: Throwable) {
                    capturedException = e
                }
            }

            launch {
                delay(2000L)
            }
        }

        capturedException.shouldNotBeNull()
    }

    /**
     * Supervisor job을 사용하면, 다른 child job이 예외를 발생시키더라도 다른 child job은 계속 실행됩니다.
     */
    @Test
    fun `SupervisorJob을 활용하여 예외처리하기`() = runTest {
        val job = SupervisorJob()
        val scope = CoroutineScope(job)
        var run2: Boolean = false

        scope.launch {
            delay(100L)
            throw RuntimeException("Boom!")
        }

        scope.launch {
            delay(200L)
            run2 = true
            log.info { "이 코드는 실행되어야 합니다" }
        }

        job.complete()
        job.join()
        run2.shouldBeTrue()
    }

    /**
     * SupervisorJob 은 child job 중 하나가 예외를 일으켜도, 다른 child job 은 실행시킵니다.
     */
    @Test
    fun `SupervisorJob을 잘 못 사용하는 예`() = runSuspendTest {
        var secondJob = false
        // 이렇게 주입해버리면, parent job 으로서 complete(), join() 을 못하므로 문제가 발생합니다.
        val job = launch(SupervisorJob()) {
            // 예외를 전파시켜 버립니다.
            launch {
                delay(100)
                throw RuntimeException("Boom!")
            }
            launch {
                // 위의 Job
                delay(200)
                secondJob = true
                log.error { "출력되지 않습니다." }
            }
        }
        job.join()
        // secondJob 이 실행되지 않습니다. - SupervisorJob 잘 못 적용한 사례
        secondJob.shouldBeFalse()
    }

    @Test
    fun `SupervisorJob을 이용하여 예외 시에도 다른 모든 Child를 실행하기`() = runSuspendTest {
        var runJob2 = false

        val job = SupervisorJob()
        // 예외를 전파시키지 않습니다.
        launch(job) {
            delay(100)
            throw RuntimeException("Boom!")
        }
        launch(job) {
            delay(200)
            runJob2 = true
            log.info { "출력되어야 합니다" }
        }
        job.complete()
        job.join()
        runJob2.shouldBeTrue()
    }

    @Test
    fun `supervisorScope 를 이용하여 예외 전파를 래핑하기`() = runSuspendTest {
        var runJob2 = false

        // supervisorScope 를 이용하면 child 간의 예외에 대한 영향을 받지 않습니다.
        val job = supervisorScope {
            // 예외가 발생하지만 전파시키지 않습니다.
            launch {
                delay(100)
                throw RuntimeException("Boom!")
            }
            launch {
                delay(200)
                runJob2 = true
                log.info { "출력되어야 합니다" }
            }
        }
        job.join()
        log.info { "Done" }
        runJob2.shouldBeTrue()
    }

    @Test
    fun `suspervisorScope와 await 함수에서 예외 전파를 막기`() = runTest {
        // supervisorScope 환경 하에서 children 을 독립적으로 실행할 수 있습니다.
        supervisorScope {
            val str1 = async<String> {
                delay(100)
                throw RuntimeException("Boom!")
            }
            val str2 = async {
                delay(200)
                "Text2"
            }

            // supervisorScope 덕분에 str1 용 async 에서 예외가 발생하더라도 str2용 async는 예외 전파없이 실행 된다.
            try {
                str1.await()
            } catch (e: Throwable) {
                log.info { "예외를 잡았습니다" }
            }
            log.info { "str2=${str2.await()}" }
        }
    }

    /**
     * [CancellationException]은 부모에게 전파되지 않습니다.
     */
    object MyNonPropagatingException: CancellationException()

    @Test
    fun `자식이 취소된다고 부모가 취소되지는 않습니다`() = runTest {
        var runJob2 = false

        // 자식 1
        launch {
            // 손자 1
            launch {
                delay(100)
                fail("취소되었기 때문에 실행되면 안됩니다.")
            }
            throw MyNonPropagatingException
        }

        // 자식 2 - 자식 1과 상관없이 실행된다.
        launch {
            delay(100)
            runJob2 = true
            log.info { "자식1과 상관없이 실행됩니다." }
        }.join()

        runJob2.shouldBeTrue()
    }

    @Test
    fun `Coroutine exception handler 를 사용하여 작업하기`() = runTest {
        var hasException = false

        val handler = CoroutineExceptionHandler { _, exception ->
            hasException = true
            log.error(exception) { "예외가 발생했습니다." }
        }

        val job = SupervisorJob()
        val scope = CoroutineScope(job + handler)

        // 예외 발생 시 exception handler 가 처리합니다.
        scope.launch {
            delay(100)
            throw RuntimeException("Boom!")
        }
        scope.launch {
            delay(200)
            log.info { "이 코드는 출력되어야 합니다." }
        }

        job.complete()
        job.join()
        hasException.shouldBeTrue()
    }
}
