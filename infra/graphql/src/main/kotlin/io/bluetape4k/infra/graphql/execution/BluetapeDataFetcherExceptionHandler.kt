package io.bluetape4k.infra.graphql.execution

import com.netflix.graphql.dgs.exceptions.DgsException
import com.netflix.graphql.types.errors.TypedGraphQLError
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import io.bluetape4k.concurrent.futureOf
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.error
import io.bluetape4k.support.classIsPresent
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

class BluetapeDataFetcherExceptionHandler: DataFetcherExceptionHandler {

    companion object: KLogging() {
        private val springSecurityAvailable: Boolean by lazy {
            classIsPresent(
                "org.springframework.security.access.AccessDeniedException",
                BluetapeDataFetcherExceptionHandler::class.java.classLoader
            )
        }

        private fun isSpringSecurityAccessException(exception: Throwable?): Boolean {
            if (!springSecurityAvailable || exception == null) {
                return false
            }
            return try {
                exception is org.springframework.security.access.AccessDeniedException
            } catch (e: Throwable) {
                log.debug(e) { "Unable to verify if exception is a Spring Security exception" }
                false
            }
        }
    }

    override fun handleException(
        handlerParameters: DataFetcherExceptionHandlerParameters,
    ): CompletableFuture<DataFetcherExceptionHandlerResult> {
        return futureOf { doHandleException(handlerParameters) }
    }

    private fun doHandleException(
        handlerParameters: DataFetcherExceptionHandlerParameters,
    ): DataFetcherExceptionHandlerResult {
        val exception = unwrapCompletionException(handlerParameters.exception)
        log.error(exception) { "Exception while executing data fetcher for ${handlerParameters.path}" }

        val graphqlError = when (exception) {
            is DgsException -> exception.toGraphQlError(handlerParameters.path)
            else            -> when {
                isSpringSecurityAccessException(exception) -> TypedGraphQLError.newPermissionDeniedBuilder()
                else                                       -> TypedGraphQLError.newInternalErrorBuilder()
            }.message("%s: %s", exception.javaClass.name, exception.message)
                .apply { handlerParameters.path?.let { path(it) } }
                .build()
        }

        return DataFetcherExceptionHandlerResult.newResult()
            .error(graphqlError)
            .build()
    }

    private fun unwrapCompletionException(exception: Throwable): Throwable = when (exception) {
        is CompletionException -> exception.cause ?: exception
        else                   -> exception
    }

}
