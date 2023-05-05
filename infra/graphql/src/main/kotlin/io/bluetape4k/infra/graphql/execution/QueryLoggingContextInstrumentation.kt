package io.bluetape4k.infra.graphql.execution

import graphql.ExecutionResult
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.SimpleInstrumentation
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters
import io.bluetape4k.io.cryptography.digest.Digesters
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.withLoggingContext

/**
 * Query Hash를 Slf4j MDC에 추가하는 Instrumentation
 */
class QueryLoggingContextInstrumentation: SimpleInstrumentation() {

    companion object: KLogging() {
        private val SHA256 = Digesters.SHA256
    }

    override fun beginExecution(
        parameters: InstrumentationExecutionParameters,
    ): InstrumentationContext<ExecutionResult> {
        val queryHash = SHA256.digest(parameters.query)

        return withLoggingContext("graphql.query.hash" to queryHash) {
            super.beginExecution(parameters)
        }
    }
}
