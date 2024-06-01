package io.bluetape4k.graphql.dgs.coroutines

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.TypeRef
import com.jayway.jsonpath.spi.mapper.MappingException
import com.netflix.graphql.dgs.exceptions.DgsQueryExecutionDataExtractionException
import com.netflix.graphql.dgs.exceptions.QueryException
import com.netflix.graphql.dgs.internal.BaseDgsQueryExecutor
import com.netflix.graphql.dgs.internal.DefaultDgsQueryExecutor
import com.netflix.graphql.dgs.internal.DgsDataLoaderProvider
import com.netflix.graphql.dgs.internal.DgsSchemaProvider
import com.netflix.graphql.dgs.internal.QueryValueCustomizer
import com.netflix.graphql.dgs.reactive.DgsReactiveQueryExecutor
import com.netflix.graphql.dgs.reactive.internal.DefaultDgsReactiveGraphQLContextBuilder
import com.netflix.graphql.dgs.reactive.internal.DgsReactiveRequestData
import graphql.ExecutionResult
import graphql.execution.ExecutionIdProvider
import graphql.execution.ExecutionStrategy
import graphql.execution.NonNullableFieldWasNullError
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.preparsed.PreparsedDocumentProvider
import graphql.schema.GraphQLSchema
import io.bluetape4k.json.jackson.Jackson
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.error
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.updateAndGet
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import java.util.*

/**
 * [DefaultDgsReactiveQueryExecutor] 를 Coroutines 방식으로 구현한 구현체
 */
class DefaultDgsCoroutineQueryExecutor(
    defaultSchema: GraphQLSchema,
    private val schemaProvider: DgsSchemaProvider,
    private val dataLoaderProvider: DgsDataLoaderProvider,
    private val contextBuilder: DefaultDgsReactiveGraphQLContextBuilder,
    private val instrumentation: Instrumentation?,
    private val queryExecutionStrategy: ExecutionStrategy,
    private val mutationExecutionStrategy: ExecutionStrategy,
    private val idProvider: Optional<ExecutionIdProvider>,
    private val reloadIndicator: DefaultDgsQueryExecutor.ReloadSchemaIndicator = DefaultDgsQueryExecutor.ReloadSchemaIndicator { false },
    private val preparsedDocumentProvider: PreparsedDocumentProvider? = null,
    private val queryValueCustomizer: QueryValueCustomizer = QueryValueCustomizer { query -> query },
): DgsReactiveQueryExecutor {

    companion object: KLogging()

    private val schema: AtomicRef<GraphQLSchema> = atomic(defaultSchema)

    override fun execute(
        query: String?,
        variables: MutableMap<String, Any>?,
        extensions: MutableMap<String, Any>?,
        headers: HttpHeaders?,
        operationName: String?,
        serverRequest: ServerRequest?,
    ): Mono<ExecutionResult> = mono {
        val gqlSchema = when {
            reloadIndicator.reloadSchema() -> schema.updateAndGet { schemaProvider.schema() }
            else                           -> schema.value
        }

        val dgsContext = contextBuilder.build(DgsReactiveRequestData(extensions, headers, serverRequest)).awaitSingle()

        val result = BaseDgsQueryExecutor.baseExecute(
            query = queryValueCustomizer.apply(query),
            variables = variables,
            extensions = extensions,
            operationName = operationName,
            dgsContext = dgsContext,
            graphQLSchema = gqlSchema,
            dataLoaderProvider = dataLoaderProvider,
            instrumentation = instrumentation,
            queryExecutionStrategy = queryExecutionStrategy,
            mutationExecutionStrategy = mutationExecutionStrategy,
            idProvider = idProvider,
            preparsedDocumentProvider = preparsedDocumentProvider
        ).await()

        val nullValueError = result?.errors?.find { it is NonNullableFieldWasNullError }
        if (nullValueError != null) {
            log.error { nullValueError.message }
        }
        result
    }

    override fun <T: Any?> executeAndExtractJsonPath(
        query: String,
        jsonPath: String,
        variables: MutableMap<String, Any>,
        serverRequest: ServerRequest?,
    ): Mono<T> = mono {
        getJsonResult(query, variables, serverRequest)
            .let { JsonPath.read(it, jsonPath) }
    }

    override fun executeAndGetDocumentContext(
        query: String,
        variables: MutableMap<String, Any>,
    ): Mono<DocumentContext> = mono {
        getJsonResult(query, variables, null)
            .let { BaseDgsQueryExecutor.parseContext.parse(it) }
    }

    override fun <T: Any?> executeAndExtractJsonPathAsObject(
        query: String,
        jsonPath: String,
        variables: MutableMap<String, Any>,
        clazz: Class<T>,
    ): Mono<T> = mono {
        getJsonResult(query, variables, null)
            .let { BaseDgsQueryExecutor.parseContext.parse(it) }
            .let {
                try {
                    it.read(jsonPath, clazz)
                } catch (ex: MappingException) {
                    throw DgsQueryExecutionDataExtractionException(ex, it.jsonString(), jsonPath, clazz)
                }
            }
    }

    override fun <T: Any?> executeAndExtractJsonPathAsObject(
        query: String,
        jsonPath: String,
        variables: MutableMap<String, Any>,
        typeRef: TypeRef<T>,
    ): Mono<T> = mono {
        getJsonResult(query, variables, null)
            .let { BaseDgsQueryExecutor.parseContext.parse(it) }
            .let {
                try {
                    it.read(jsonPath, typeRef)
                } catch (ex: MappingException) {
                    throw DgsQueryExecutionDataExtractionException(ex, it.jsonString(), jsonPath, typeRef)
                }
            }
    }

    private suspend fun getJsonResult(
        query: String,
        variables: MutableMap<String, Any>?,
        serverRequest: ServerRequest?,
    ): String {
        val httpHeaders = serverRequest?.headers()?.asHttpHeaders()
        val result = execute(query, variables, null, httpHeaders, null, serverRequest).awaitSingle()
        if (result.errors.size > 0) {
            throw QueryException(result.errors)
        }
        return Jackson.defaultJsonMapper.writeValueAsString(result.toSpecification())
        // return BaseDgsQueryExecutor.objectMapper.writeValueAsString(result.toSpecification())
    }
}
