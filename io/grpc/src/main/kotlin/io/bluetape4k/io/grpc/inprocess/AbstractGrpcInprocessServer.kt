package io.bluetape4k.io.grpc.inprocess

import io.bluetape4k.io.grpc.GrpcServer
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.info
import io.bluetape4k.utils.ShutdownQueue
import io.grpc.BindableService
import io.grpc.Server
import io.grpc.inprocess.InProcessServerBuilder
import kotlinx.atomicfu.atomic
import java.util.concurrent.TimeUnit


/**
 * gRPC Inprocess Server 를 이용하여, gRPC Service를 제공하는 Server 입니다.
 *
 * @property builder [InProcessServerBuilder] instance
 * @property services array of grpc service
 */
abstract class AbstractGrpcInprocessServer(
    builder: InProcessServerBuilder,
    vararg services: BindableService,
): GrpcServer {

    constructor(name: String, vararg services: BindableService): this(InProcessServerBuilder.forName(name), *services)
    constructor(port: Int, vararg services: BindableService): this(InProcessServerBuilder.forPort(port), *services)

    companion object: KLogging()

    private val server: Server by lazy {
        builder.apply { services.forEach { addService(it) } }.build()
    }

    private val running = atomic(false)

    override val isRunning: Boolean by running
    override val isShutdown: Boolean get() = server.isShutdown

    @Synchronized
    override fun start() {
        log.debug { "Starting InProcess gRPC Server..." }
        server.start()
        log.info { "Start InProcess gRPC Server." }
        running.value = true

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
                server.shutdown().awaitTermination(5, TimeUnit.SECONDS)
            }
            running.value = false
        }
    }
}
