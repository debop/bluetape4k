package io.bluetape4k.io.grpc.testing.integration

import io.bluetape4k.io.grpc.AbstractGrpcClient
import io.bluetape4k.io.grpc.managedChannel
import io.bluetape4k.io.grpc.testing.integration.Messages.StreamingInputCallRequest
import io.bluetape4k.io.grpc.testing.integration.Messages.StreamingInputCallResponse
import io.bluetape4k.io.grpc.testing.integration.Messages.StreamingOutputCallRequest
import io.bluetape4k.io.grpc.testing.integration.Messages.StreamingOutputCallResponse
import io.bluetape4k.logging.KLogging
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import java.util.concurrent.ForkJoinPool
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class TestServiceClient private constructor(channel: ManagedChannel): AbstractGrpcClient(channel) {

    companion object: KLogging() {
        private val random = Random(System.currentTimeMillis())

        @JvmStatic
        operator fun invoke(channel: ManagedChannel): TestServiceClient {
            check(!channel.isShutdown) { "Channel must not be shutdown." }
            return TestServiceClient(channel)
        }

        @JvmStatic
        operator fun invoke(host: String = "localhost", port: Int = 8080): TestServiceClient {
            return invoke(managedChannel(host, port) {
                usePlaintext()
                executor(ForkJoinPool.commonPool())
            })
        }

        @JvmStatic
        @JvmOverloads
        operator fun invoke(
            channelBuilder: ManagedChannelBuilder<*>,
            dispatcher: CoroutineDispatcher = Dispatchers.IO,
        ): TestServiceClient {
            return invoke(channelBuilder.executor(dispatcher.asExecutor()).build())
        }
    }

    val stub = TestServiceGrpcKt.TestServiceCoroutineStub(channel)

    fun emptyCall() = runBlocking<Unit> {
        stub.emptyCall(Emptys.Empty.getDefaultInstance())
    }

    suspend fun unaryCall(request: Messages.SimpleRequest): Messages.SimpleResponse {
        return stub.unaryCall(request)
    }

    fun streamingOutputCall(request: StreamingOutputCallRequest): Flow<StreamingOutputCallResponse> {
        return stub.streamingOutputCall(request)
    }

    suspend fun stremingInputCall(requests: Flow<StreamingInputCallRequest>): StreamingInputCallResponse {
        return stub.streamingInputCall(requests)
    }

    fun fullDuplexCall(requests: Flow<StreamingOutputCallRequest>): Flow<StreamingOutputCallResponse> {
        return stub.fullDuplexCall(requests)
    }

    fun halfDuplexCall(requests: Flow<StreamingOutputCallRequest>): Flow<StreamingOutputCallResponse> {
        return stub.halfDuplexCall(requests)
    }
}
