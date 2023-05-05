package io.bluetape4k.infra.graphql.dgs

import com.netflix.graphql.dgs.context.DgsCustomContextBuilderWithRequest
import io.bluetape4k.infra.graphql.LoggingContextProvider
import org.springframework.http.HttpHeaders
import org.springframework.web.context.request.WebRequest

abstract class AbstractDgsContextBuilder<T: LoggingContextProvider>: DgsCustomContextBuilderWithRequest<T> {

    override fun build(
        extensions: Map<String, Any>?,
        headers: HttpHeaders?,
        webRequest: WebRequest?,
    ): T {
        return doBuild(extensions, headers, webRequest)
    }

    protected abstract fun doBuild(
        extensions: Map<String, Any>?,
        headers: HttpHeaders?,
        webRequest: WebRequest?,
    ): T
}
