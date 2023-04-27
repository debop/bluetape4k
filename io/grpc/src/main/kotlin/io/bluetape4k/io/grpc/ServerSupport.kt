package io.bluetape4k.io.grpc

import io.grpc.Server
import io.grpc.ServerBuilder


/**
 * [ServerBuilder]를 생성하고, 추가 설정을 수행합니다.
 *
 * @param port        server port
 * @param initializer server builder initializer
 * @return [Server] instance
 */
inline fun serverBuilder(port: Int, initializer: ServerBuilder<*>.() -> Unit): ServerBuilder<*> {
    return ServerBuilder.forPort(port).apply(initializer)
}

/**
 * [ServerBuilder]를 이용하여 gRPC Server를 설정하고, [Server]를 빌드합니다.
 *
 * @param port        server port
 * @param initializer server builder initializer
 * @return [Server] instance
 */
inline fun server(port: Int, initializer: ServerBuilder<*>.() -> Unit): Server {
    return serverBuilder(port, initializer).build()
}
