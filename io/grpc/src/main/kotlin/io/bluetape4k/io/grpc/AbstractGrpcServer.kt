package io.bluetape4k.io.grpc

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.info
import io.bluetape4k.logging.warn
import io.bluetape4k.utils.ShutdownQueue
import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.ServerServiceDefinition
import kotlinx.atomicfu.atomic
import java.util.concurrent.TimeUnit


/**
 * gRPC 서비스를 제공해주는 Server의 최상위 추상화 클래스입니다.
 *
 * @property builder  builder of grpc server
 * @property services collection of grpc services
 */
abstract class AbstractGrpcServer(
    protected val builder: ServerBuilder<*>,
    protected val services: List<BindableService>,
): GrpcServer {

    constructor(port: Int, vararg services: BindableService)
        : this(ServerBuilder.forPort(port), services.toList())

    companion object: KLogging()

    protected val server: Server by lazy { createServer() }

    private val running = atomic(false)

    val port: Int get() = server.port
    val serviceDefinitions: List<ServerServiceDefinition> get() = server.services

    override val isRunning: Boolean by running
    override val isShutdown: Boolean get() = server.isShutdown

    protected open fun createServer(): Server {
        log.debug { "Create gRPC server..." }
        return builder.apply { services.forEach { addService(it) } }.build()
    }

    @Synchronized
    override fun start() {
        log.debug { "Starting gRPC Server..." }
        server.start()
        running.value = true
        log.info { "Start gRPC Server. port=$port, services=$serviceDefinitions" }

        ShutdownQueue.register {
            if (!isShutdown) {
                log.debug { "Shutdown gRPC server since JVM is shutting down." }
                stop()
                log.info { "gRPC Server is shutdowned." }
            }
        }
    }

    @Synchronized
    override fun stop() {
        if (!isShutdown) {
            runCatching {
                server.shutdown()
                if (!server.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.warn { "Timed out waiting for server shutdown" }
                }
            }
            running.value = false
        }
    }
}
