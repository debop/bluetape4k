package io.bluetape4k.workshop.otel.service

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.opentelemetry.api.trace.SpanKind
import io.opentelemetry.instrumentation.annotations.SpanAttribute
import io.opentelemetry.instrumentation.annotations.WithSpan
import org.springframework.stereotype.Component

@Component
class TracedClass {

    companion object: KLogging()

    @WithSpan
    fun tracedMethod() {
        log.debug { "tracedMethod called" }
    }

    @WithSpan(value = "custom span name")
    fun tracedMethodWithCustomSpanName() {
        log.debug { "tracedMethodWithCustomSpanName called" }
    }

    @WithSpan(kind = SpanKind.CLIENT)
    fun tracedClientSpan() {
        log.debug { "tracedClientSpan called" }
    }

    fun tracedMethodWithAttribute(@SpanAttribute("custom-attribute") customAttribute: String) {
        log.debug { "tracedMethodWithAttribute called" }
    }
}
