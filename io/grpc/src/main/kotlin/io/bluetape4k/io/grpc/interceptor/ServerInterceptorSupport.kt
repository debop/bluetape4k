package io.bluetape4k.io.grpc.interceptor

import io.grpc.ForwardingServerCall
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import io.grpc.Status


/**
 * Echo the request headers from a client into response headers and trailers.
 * Useful for testing end-to-end metadata propagation.
 *
 * @param keys header keys
 */
fun echoRequestHeadersInterceptor(vararg keys: Metadata.Key<*>): ServerInterceptor {
    val keySet = keys.toSet()

    return object: ServerInterceptor {
        override fun <ReqT, RespT> interceptCall(
            call: ServerCall<ReqT, RespT>,
            requestHeaders: Metadata,
            next: ServerCallHandler<ReqT, RespT>,
        ): ServerCall.Listener<ReqT> =
            next.startCall(
                object: ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
                    override fun sendHeaders(responseHeaders: Metadata) {
                        responseHeaders.merge(requestHeaders, keySet)
                        super.sendHeaders(responseHeaders)
                    }

                    override fun close(status: Status?, trailers: Metadata) {
                        trailers.merge(requestHeaders, keySet)
                        super.close(status, trailers)
                    }
                },
                requestHeaders
            )
    }
}

/**
 * Echoes request headers with the specified key(s) from a client into response headers only.
 *
 * @param keys header keys
 */
fun echoRequestMetadataInHeaders(vararg keys: Metadata.Key<*>): ServerInterceptor {
    val keySet = keys.toSet()
    return object: ServerInterceptor {
        override fun <ReqT, RespT> interceptCall(
            call: ServerCall<ReqT, RespT>,
            requestHeaders: Metadata,
            next: ServerCallHandler<ReqT, RespT>,
        ): ServerCall.Listener<ReqT> =
            next.startCall(
                object: ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
                    override fun sendHeaders(responseHeaders: Metadata?) {
                        responseHeaders?.merge(requestHeaders, keySet)
                        super.sendHeaders(responseHeaders)
                    }
                },
                requestHeaders
            )
    }
}

/**
 * Echoes request headers with the specified key(s) from a client into response trailers only.
 *
 * @param keys header keys
 */
fun echoRequestMetadataInTrailers(vararg keys: Metadata.Key<*>): ServerInterceptor {
    val keySet = keys.toSet()
    return object: ServerInterceptor {
        override fun <ReqT, RespT> interceptCall(
            call: ServerCall<ReqT, RespT>,
            requestHeaders: Metadata,
            next: ServerCallHandler<ReqT, RespT>,
        ): ServerCall.Listener<ReqT> =
            next.startCall(
                object: ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
                    override fun close(status: Status, trailers: Metadata) {
                        trailers.merge(requestHeaders, keySet)
                        super.close(status, trailers)
                    }
                },
                requestHeaders
            )
    }
}
