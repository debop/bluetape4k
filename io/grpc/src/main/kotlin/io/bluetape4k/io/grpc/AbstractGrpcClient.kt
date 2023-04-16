package io.bluetape4k.io.grpc

import io.bluetape4k.logging.KLogging
import io.grpc.ManagedChannel
import java.io.Closeable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

/**
 * gRPC 통신을 수행하는 Client의 최상위 추상화 클래스입니다.
 */
abstract class AbstractGrpcClient(protected val channel: ManagedChannel): Closeable {

    constructor(host: String = DEFAULT_HOST, port: Int = DEFAULT_PORT): this(buildForAddress(host, port))

    companion object: KLogging() {

        const val DEFAULT_HOST = "localhost"
        const val DEFAULT_PORT = 50051

        private fun buildForAddress(host: String, port: Int): ManagedChannel =
            managedChannel(host, port) {
                usePlaintext()
                executor(Dispatchers.IO.asExecutor())
            }
    }

    override fun close() {
        if (!channel.isShutdown) {
            runCatching { channel.shutdown() }
        }
    }
}
