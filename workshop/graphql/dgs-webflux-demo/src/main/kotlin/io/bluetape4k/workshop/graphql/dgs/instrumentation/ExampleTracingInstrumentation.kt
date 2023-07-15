package io.bluetape4k.workshop.graphql.dgs.instrumentation

import graphql.ExecutionResult
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.InstrumentationState
import graphql.execution.instrumentation.SimplePerformantInstrumentation
import graphql.execution.instrumentation.parameters.InstrumentationCreateStateParameters
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import graphql.schema.DataFetcher
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.error
import io.bluetape4k.logging.info
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class ExampleTracingInstrumentation: SimplePerformantInstrumentation() {

    companion object: KLogging()

    private var state: TraceState? = null

    override fun createState(parameters: InstrumentationCreateStateParameters): InstrumentationState? {
        log.debug { "Create TraceState. parameters=$parameters" }
        state = TraceState()
        return state
    }

    override fun beginExecution(parameters: InstrumentationExecutionParameters): InstrumentationContext<ExecutionResult> {
        if (state != null) {
            state!!.traceStartTime = System.currentTimeMillis()
            log.debug { "Begin execution. state=$state" }
        }
        return super.beginExecution(parameters)
    }

    /**
     * DataFetcher 의 성능 측정을 위해 동기, 비동기 실행 후 실행 시간을 로그로 남긴다
     */
    override fun instrumentDataFetcher(
        dataFetcher: DataFetcher<*>,
        parameters: InstrumentationFieldFetchParameters,
    ): DataFetcher<*> {
        if (parameters.isTrivialDataFetcher) {
            return dataFetcher
        }

        return DataFetcher { env ->
            val startTime = System.currentTimeMillis()
            val result = dataFetcher.get(env)

            when (result) {
                is CompletableFuture<*> -> result.whenComplete { _, error ->
                    if (error != null) {
                        log.error(error) { "DataFetcher ${findDataFetcherTag(parameters)} execution failed" }
                    } else {
                        reportExecutionTimes(parameters, startTime)
                    }
                }

                else                    ->
                    reportExecutionTimes(parameters, startTime)
            }
            result
        }
    }

    private fun reportExecutionTimes(
        parameters: InstrumentationFieldFetchParameters,
        startTime: Long,
    ) {
        val totalTime = System.currentTimeMillis() - startTime
        log.info { "DataFetcher ${findDataFetcherTag(parameters)} execution time: $totalTime msec" }
    }

    override fun instrumentExecutionResult(
        executionResult: ExecutionResult,
        parameters: InstrumentationExecutionParameters,
    ): CompletableFuture<ExecutionResult> {
        if (state != null) {
            val totalTime = System.currentTimeMillis() - state!!.traceStartTime
            log.info { "Total execution time: $totalTime msec" }
        }

        return super.instrumentExecutionResult(executionResult, parameters)
    }

    private fun findDataFetcherTag(
        parameters: InstrumentationFieldFetchParameters,
    ): String {
        val type = parameters.executionStepInfo.parent.type
        val parentType = when (type) {
            is GraphQLNonNull -> type.wrappedType
            else              -> type
        } as GraphQLObjectType

        return "${parentType.name}.${parameters.executionStepInfo.path.segmentName}"
    }

    data class TraceState(var traceStartTime: Long = 0L): InstrumentationState
}
