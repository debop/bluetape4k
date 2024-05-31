package io.bluetape4k.concurrent.virtualthread

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.warn
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class VirtualThreadSupportTest: AbstractVirtualThreadTest() {

    companion object: KLogging()

    @Test
    fun `platformThreadBuilder를 이용하여 PlatformThread 생성하기`() {
        val builder = platformThreadBuilder {
            daemon(false)
            priority(10)
            stackSize(1024)
            name("platform-thread")
            inheritInheritableThreadLocals(false)
            uncaughtExceptionHandler { thread, ex ->
                log.warn(ex) { "Thread[$thread] failed with exception." }
            }
        }

        log.debug { "Builder class=${builder.javaClass.name}" }
        builder.javaClass.name shouldBeEqualTo "java.lang.ThreadBuilders\$PlatformThreadBuilder"

        val thread = builder.unstarted {
            log.debug { "Unstarted Platform Thread" }
        }
        with(thread) {
            javaClass.name shouldBeEqualTo "java.lang.Thread"
            name shouldBeEqualTo "platform-thread"
            isDaemon shouldBeEqualTo false
            priority shouldBeEqualTo 10
        }
        thread.start()
        thread.join()
    }

    @Test
    fun `virtualThreadBuilder 를 이용하여 VirtualThread 생성하기`() {
        val builder = virtualThreadBuilder {
            name("virtual-thread")
            inheritInheritableThreadLocals(false)
            uncaughtExceptionHandler { thread, ex ->
                log.warn(ex) { "Thread[$thread] failed with exception." }
            }
        }

        log.debug { "Builder class=${builder.javaClass.name}" }
        builder.javaClass.name shouldBeEqualTo "java.lang.ThreadBuilders\$VirtualThreadBuilder"

        val thread = builder.unstarted {
            log.debug { "Unstarted Virtual Thread" }
        }
        with(thread) {
            javaClass.name shouldBeEqualTo "java.lang.VirtualThread"
            name shouldBeEqualTo "virtual-thread"
        }
        thread.start()
        thread.join()
    }

    @Test
    fun `virtualThreadFactory 를 이용하여 virtual thread 생성하기`() {
        val factory = virtualThreadFactory {
            name("virtual-thread")
            inheritInheritableThreadLocals(false)
            uncaughtExceptionHandler { thread, ex ->
                log.warn(ex) { "Thread[$thread] failed with exception." }
            }
        }

        log.debug { "Factory class=${factory.javaClass.name}" }
        factory.javaClass.name shouldBeEqualTo "java.lang.ThreadBuilders\$VirtualThreadFactory"

        val thread = factory.newThread {
            log.debug { "Virtual Thread" }
        }
        with(thread) {
            javaClass.name shouldBeEqualTo "java.lang.VirtualThread"
            name shouldBeEqualTo "virtual-thread"
            isDaemon shouldBeEqualTo true
            priority shouldBeEqualTo 5
        }
        // Thread가 시작되지 않았으므로 NEW 상태
        thread.state shouldBeEqualTo Thread.State.NEW

        thread.start()
        // Thread가 시작되었으므로 RUNNABLE 상태
        thread.state shouldBeEqualTo Thread.State.RUNNABLE

        thread.join()
    }

    @Test
    fun `Virtual Thread 자동 시작하기`() {
        val thread = virtualThread(start = true, prefix = "active-thread-") {
            sleep(1000)
            log.debug { "Virtual Thread running" }
        }
        // Thread가 시작되었으므로 RUNNABLE 상태
        thread.state shouldBeEqualTo Thread.State.RUNNABLE
        thread.join()
    }

    @Test
    fun `Virtual Thread 수동 시작하기 `() {
        val thread = virtualThread(start = false, prefix = "passive-thread-") {
            sleep(1000)
            log.debug { "Virtual Thread running" }
        }
        // Thread가 시작되지 않았으므로 NEW 상태
        thread.state shouldBeEqualTo Thread.State.NEW

        thread.start()
        // Thread가 시작되었으므로 RUNNABLE 상태
        thread.state shouldBeEqualTo Thread.State.RUNNABLE

        thread.join()
    }
}
