package io.bluetape4k.infra.otel

import io.bluetape4k.infra.otel.metrics.loggingMetricExporterOf
import io.bluetape4k.infra.otel.metrics.periodicMetricReader
import io.bluetape4k.infra.otel.metrics.sdkMeterProvider
import io.bluetape4k.infra.otel.trace.loggingSpanExporterOf
import io.bluetape4k.infra.otel.trace.sdkTracerProvider
import io.bluetape4k.infra.otel.trace.simpleSpanProcessorOf
import io.bluetape4k.io.utils.Resourcex
import io.bluetape4k.logging.KLogging
import io.opentelemetry.api.OpenTelemetry
import org.junit.jupiter.params.provider.Arguments
import java.time.Duration
import java.util.logging.LogManager

abstract class AbstractOtelTest {

    companion object: KLogging() {
        init {
            /**
             * Java logging configuration for Slf4j
             *
             * 참고 : [Using java.util.logging and Slf4j together](https://medium.com/@a.petrivskyy/using-java-util-logging-and-slf4j-together-e7f2ee1d712b)
             */
            Resourcex.getInputStream("/logging.properties").use {
                LogManager.getLogManager().readConfiguration(it)
            }
        }

        const val JUNIT_METHOD_SOURCE_NAME = "getOpenTelemetries"
    }

    val loggingOtel: OpenTelemetry by lazy { initOpenTelemetry() }

    private fun initOpenTelemetry(): OpenTelemetry {

        // Tracer provider configured to export spans with SimpleSpanProcessor
        // using the logging exporter.
        val tracerProvider = sdkTracerProvider {
            addSpanProcessor(simpleSpanProcessorOf(loggingSpanExporterOf()))
        }

        val meterProvider = sdkMeterProvider {
            // Create an instance of PeriodicMetricReader and configure it
            // to export via the logging exporter
            val reader = periodicMetricReader(loggingMetricExporterOf()) {
                setInterval(Duration.ofMillis(500L))
            }
            registerMetricReader(reader)
        }

        return openTelemetrySdk {
            setTracerProvider(tracerProvider)
            setMeterProvider(meterProvider)
        }
    }

    protected fun getOpenTelemetries() = listOf<Arguments>(
        Arguments.of("global", globalOpenTelemetry),
        Arguments.of("logging", loggingOtel)
    )
}
