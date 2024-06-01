package io.bluetape4k.graphql.dgs

import com.netflix.graphql.dgs.reactive.DgsReactiveCustomContextBuilderWithRequest
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono

abstract class AbstractDgsCoroutineContextBuilder<T: io.bluetape4k.graphql.LoggingContextProvider>
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
