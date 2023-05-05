package io.bluetape4k.infra.graphql.dgs

import com.netflix.graphql.dgs.reactive.DgsReactiveCustomContextBuilderWithRequest
import io.bluetape4k.infra.graphql.LoggingContextProvider
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono

abstract class AbstractDgsCoroutineContextBuilder<T: LoggingContextProvider>
    : DgsReactiveCustomContextBuilderWithRequest<T> {

    override fun build(
        extensions: Map<String, Any>?,
        headers: HttpHeaders?,
        serverRequest: ServerRequest?,
    ): Mono<T> = mono {
        doBuild(extensions, headers, serverRequest)
    }

    protected abstract suspend fun doBuild(
        extensions: Map<String, Any>?,
        headers: HttpHeaders?,
        serverRequest: ServerRequest?,
    ): T
}
