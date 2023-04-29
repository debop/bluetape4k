package io.bluetape4k.infra.otel.examples.logging

import io.bluetape4k.infra.otel.AbstractOtelTest
import io.bluetape4k.infra.otel.coroutines.useSpanAwait
import io.bluetape4k.infra.otel.trace.useSpan
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test

class LoggingExporterExample: AbstractOtelTest() {

    companion object: KLogging() {
        private val INSTRUMENTATION_NAME: String = LoggingExporterExample::class.java.name
    }

    private val tracer = loggingOtel.getTracer(INSTRUMENTATION_NAME)
    private val counter = loggingOtel.getMeter(INSTRUMENTATION_NAME).counterBuilder("work_done").setUnit("ea").build()
    private val counterAsync =
        loggingOtel.getMeter(INSTRUMENTATION_NAME).counterBuilder("work_done_async").setUnit("ea").build()

    // it is important to initialize your SDK as early as possible in your application's lifecycle
    @Test
    fun `export to logging`() {
        // Generate a few sample spans
        repeat(5) {
            myWonderfulUseCase()
        }
        Thread.sleep(1000L)
    }

    private fun myWonderfulUseCase() {
        // 새로운 span 에서 작업을 수행합니다.
        tracer.spanBuilder("start my wonderful use case").useSpan { span ->
            span.addEvent("Event 0")
            log.debug { "Call doWork() ..." }
            doWork()
            span.addEvent("Event 1")
        }
    }

    private fun doWork() {
        // 새로운 span 에서 작업을 수행합니다.
        tracer.spanBuilder("doWork").useSpan { span ->
            log.debug { "Start doWork... $span" }
            Thread.sleep(500)
            log.debug { "Finish doWork. and increase work count." }
            // LongCounter의 counter 값을 1씩 증가시킵니다.
            counter.add(1)
        }
    }

    @Test
    fun `export to logger with coroutines operation`() = runSuspendWithIO {
        // Generate a few sample spans
        val jobs = List(5) {
            launch { myWonderfulUseCaseAsync() }
        }
        jobs.joinAll()
        delay(1000)
    }

    private suspend fun myWonderfulUseCaseAsync() {
        // 새로운 span 에서 작업을 수행합니다.
        tracer.spanBuilder("start my wonderful use case async").useSpanAwait { span ->
            span.addEvent("Event Async 0")
            log.debug { "Call doWorkAsync() ..." }
            doWorkAsync()
            span.addEvent("Event Async 1")
        }
    }

    private suspend fun doWorkAsync() {
        // 새로운 span 에서 작업을 수행합니다.
        tracer.spanBuilder("doWorkAsync").useSpanAwait { span ->
            log.debug { "Start doWorkAsync... $span" }
            delay(1000)
            log.debug { "Finish doWorkAsync. and increase work count." }
            // LongCounter의 counter 값을 1씩 증가시킵니다.
            counterAsync.add(1)
        }
    }
}
