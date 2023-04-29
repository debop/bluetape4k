package io.bluetape4k.infra.otel.examples.javaagent

import io.bluetape4k.infra.otel.common.attributesOf
import io.bluetape4k.infra.otel.common.toAttributeKey
import io.bluetape4k.infra.otel.coroutines.useSpanAwait
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.opentelemetry.api.OpenTelemetry
import kotlinx.coroutines.delay
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.random.Random

@RestController
class IndexController(@Autowired private val openTelemetry: OpenTelemetry) {

    companion object: KLogging()

    private val tracer = openTelemetry.getTracer(IndexController::class.java.name)
    private val meter = openTelemetry.getMeter(IndexController::class.java.name)

    // 메소드별 작업 시간을 측정하여 기록하는 Histogram
    private val executionTimeHistogram = meter.histogramBuilder("execution_time").build()

    private val attrMethod = "method".toAttributeKey()

    @GetMapping("/ping")
    suspend fun ping(): String {
        val sleepTime = Random.nextInt(100, 500)
        doWork(sleepTime)
        // 수행 시간을 기록한다
        executionTimeHistogram.record(sleepTime.toDouble(), attributesOf(attrMethod, "ping"))
        return "pong"
    }

    private suspend fun doWork(sleepTime: Int) {
        log.debug { "Start doWork() in doWork span ..." }
        tracer.spanBuilder("doWork").useSpanAwait { span ->
            delay(sleepTime.toLong())
            log.debug { "A sample log message! span=$span" }
        }
    }
}
