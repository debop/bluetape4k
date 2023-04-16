package io.bluetape4k.io.grpc.testing.integration

import io.bluetape4k.io.grpc.AbstractGrpcServer
import io.bluetape4k.logging.KLogging
import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.ServerInterceptors
import java.util.concurrent.Executors

class TestServiceServer private constructor(
    builder: ServerBuilder<*>,
    services: List<BindableService>,
): AbstractGrpcServer(builder, services) {

    companion object: KLogging() {

        operator fun invoke(port: Int): TestServiceServer {
            return invoke(ServerBuilder.forPort(port))
        }

        operator fun invoke(builder: ServerBuilder<*>): TestServiceServer {
            return TestServiceServer(builder, listOf(TestServiceImpl()))
        }
    }

    override fun createServer(): Server {
        val executor = Executors.newSingleThreadScheduledExecutor()
        return builder
            .addService(ServerInterceptors.intercept(TestServiceImpl(executor), TestServiceImpl.interceptors))
            .build()
    }
}
