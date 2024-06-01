package io.bluetape4k.graphql.dgs.coroutines

import com.netflix.graphql.dgs.context.DgsContext
import com.netflix.graphql.dgs.context.ReactiveDgsContext
import com.netflix.graphql.dgs.reactive.internal.DgsReactiveRequestData
import io.bluetape4k.graphql.dgs.AbstractDgsCoroutineContextBuilder
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpHeaders
import reactor.core.publisher.Mono

class DefaultDgsCoroutineGraphQLContextBuilder(
    private val dgsCoroutineContextBuilder: AbstractDgsCoroutineContextBuilder<*>? = null,
) {

    companion object: KLogging()

    suspend fun build(requestData: DgsReactiveRequestData?): DgsContext {
        val customContext = dgsCoroutineContextBuilder
            ?.build(
                requestData?.extensions ?: mapOf(),
                HttpHeaders.readOnlyHttpHeaders(requestData?.headers ?: HttpHeaders.EMPTY),
                requestData?.serverRequest,
            )
            ?: Mono.empty()

        return Mono
            .deferContextual { context ->
                customContext
                    .map { ReactiveDgsContext(it, requestData, context) }
                    .defaultIfEmpty(ReactiveDgsContext(requestData = requestData, reactorContext = context))
            }
            .awaitSingle()
    }
}
