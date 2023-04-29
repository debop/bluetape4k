package io.bluetape4k.infra.otel.examples.javaagent

import io.bluetape4k.infra.otel.metrics.loggingMetricExporterOf
import io.bluetape4k.infra.otel.metrics.periodicMetricReader
import io.bluetape4k.infra.otel.metrics.sdkMeterProvider
import io.bluetape4k.infra.otel.openTelemetrySdk
import io.bluetape4k.infra.otel.trace.loggingSpanExporterOf
import io.bluetape4k.infra.otel.trace.sdkTracerProvider
import io.bluetape4k.infra.otel.trace.simpleSpanProcessorOf
import io.opentelemetry.api.OpenTelemetry
import java.time.Duration
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class OtelSpringBootApp {

    @Bean
    fun openTelemetry(): OpenTelemetry {
        // Tracer provider configured to export spans with SimpleSpanProcessor using the logging exporter.
        val tracerProvider = sdkTracerProvider {
            addSpanProcessor(simpleSpanProcessorOf(loggingSpanExporterOf()))
        }
        val meterProvider = sdkMeterProvider {
            // Create an instance of PeriodicMetricReader and configure it to export via the logging exporter
            val metricReader = periodicMetricReader(loggingMetricExporterOf()) {
                setInterval(Duration.ofMillis(1000L))
            }
            registerMetricReader(metricReader)
        }

        // javaagent 는 GlobalOpenTelemetry.get() 을 사용하므로, 여기서는 직접 생성해서 사용합니다.
        return openTelemetrySdk {
            setTracerProvider(tracerProvider)
            setMeterProvider(meterProvider)
        }
    }
}

fun main(vararg args: String) {
    runApplication<OtelSpringBootApp>(*args) {
        webApplicationType = WebApplicationType.REACTIVE
    }
}
