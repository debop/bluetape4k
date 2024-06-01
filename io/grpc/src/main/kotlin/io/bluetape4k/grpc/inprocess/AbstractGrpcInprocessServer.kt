package io.bluetape4k.grpc.inprocess

import io.bluetape4k.grpc.GrpcServer
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.info
import io.bluetape4k.utils.ShutdownQueue
import io.grpc.BindableService
import io.grpc.Server
import io.grpc.inprocess.InProcessServerBuilder
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.ReentrantLock
import java.util.concurrent.TimeUnit
import kotlin.concurrent.withLock


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
    private val lock = ReentrantLock()

    override val isRunning: Boolean by running
    override val isShutdown: Boolean get() = server.isShutdown


    override fun start() {
        lock.withLock {
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
    }

    override fun stop() {
        lock.withLock {
            if (!isShutdown) {
                runCatching {
                    server.shutdown().awaitTermination(5, TimeUnit.SECONDS)
                }
                running.value = false
            }
        }
    }
}
