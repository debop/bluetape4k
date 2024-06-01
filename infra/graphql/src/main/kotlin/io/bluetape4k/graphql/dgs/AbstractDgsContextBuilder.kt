package io.bluetape4k.graphql.dgs

import com.netflix.graphql.dgs.context.DgsCustomContextBuilderWithRequest
import org.springframework.http.HttpHeaders
import org.springframework.web.context.request.WebRequest

abstract class AbstractDgsContextBuilder<T: io.bluetape4k.graphql.LoggingContextProvider>:
    DgsCustomContextBuilderWithRequest<T> {

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
