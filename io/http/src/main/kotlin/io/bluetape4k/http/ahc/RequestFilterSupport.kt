package io.bluetape4k.http.ahc

import org.asynchttpclient.filter.FilterContext
import org.asynchttpclient.filter.FilterContext.FilterContextBuilder
import org.asynchttpclient.filter.RequestFilter

/**
 * [RequestFilter]를 생성합니다.
 *
 * @param initializer [FilterContextBuilder]를 이용하여 [FilterContext]를 생성하는 intializer
 * @return [RequestFilter] instance
 */
@JvmName("requestFilterWithBuilder")
inline fun requestFilter(crossinline initializer: FilterContextBuilder<*>.() -> Unit): RequestFilter {
    return object: RequestFilter {
        override fun <T: Any?> filter(ctx: FilterContext<T>): FilterContext<T> {
            return FilterContextBuilder<T>().apply(initializer).build()
        }
    }
}

/**
 * [RequestFilter]를 생성합니다.
 *
 * @param block [FilterContext]를 받아서 처리하는 함수
 * @return [RequestFilter] instance
 */
@JvmName("requestFilter")
inline fun requestFilter(crossinline block: (FilterContext<*>) -> Unit): RequestFilter {
    return object: RequestFilter {
        override fun <T: Any?> filter(ctx: FilterContext<T>): FilterContext<T> {
            block(ctx)
            return ctx
        }
    }
}

/**
 * [org.asynchttpclient.Request]에 Header를 추가해주는 [RequestFilter]를 생성합니다.
 *
 * @param headers
 * @return [RequestFilter] instance
 */
fun attachHeaderRequestFilterOf(headers: Map<String, Any?>): RequestFilter {
    return requestFilter { ctx ->
        headers.forEach { (name, value) ->
            ctx.request.headers.add(name, value)
        }
    }
}

/**
 * [org.asynchttpclient.Request]에 Header를 추가해주는 [RequestFilter]를 생성합니다.
 *
 * @param namesSupplier Header name 제공 함수
 * @param valueSupplier Header value 제공 함수
 * @return [RequestFilter] instance
 */
inline fun attachHeaderRequestFilterOf(
    crossinline namesSupplier: () -> Iterable<String>,
    crossinline valueSupplier: (String) -> Any?,
): RequestFilter {
    return requestFilter { ctx ->
        namesSupplier().forEach { name ->
            ctx.request.headers.add(name, valueSupplier(name))
        }
    }
}
