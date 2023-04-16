package io.bluetape4k.io.grpc.testing.integration

import com.google.protobuf.ByteString
import io.bluetape4k.io.grpc.testing.integration.Messages.BoolValue
import io.bluetape4k.io.grpc.testing.integration.Messages.Payload
import io.bluetape4k.io.grpc.testing.integration.Messages.ResponseParameters
import io.bluetape4k.io.grpc.testing.integration.Messages.StreamingInputCallRequest
import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.junit5.output.CaptureOutput
import io.bluetape4k.junit5.output.OutputCapturer
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.randomString
import io.bluetape4k.support.toUtf8Bytes
import io.grpc.ServerBuilder
import io.grpc.StatusException
import java.util.concurrent.atomic.LongAdder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@CaptureOutput
class TestServiceTest {

    companion object: KLogging() {
        private const val TEST_SERVICE_PORT = 50055
    }

    private lateinit var server: TestServiceServer
    private lateinit var client: TestServiceClient

    @BeforeAll
    fun setup() {
        server = TestServiceServer(ServerBuilder.forPort(TEST_SERVICE_PORT)).apply { start() }
        client = TestServiceClient("localhost", TEST_SERVICE_PORT)
    }

    @AfterAll
    fun cleanup() {
        client.close()
        server.close()
    }

    @Test
    fun `instancing server and client`() {
        server.shouldNotBeNull()
        server.isRunning.shouldBeTrue()
        client.shouldNotBeNull()
    }

    @Test
    fun `empty call`() {
        client.emptyCall()
    }

    @Test
    fun `unary call with simple request`() = runSuspendTest {
        val request = Messages.SimpleRequest.newBuilder()
            .apply {
                responseSize = 500
            }
            .build()

        val response = client.unaryCall(request)
        response.payload.body.isEmpty shouldBeEqualTo false
    }

    @Test
    fun `unary call with exception`() = runSuspendTest {
        val request = Messages.SimpleRequest.newBuilder()
            .apply {
                responseStatus = Messages.EchoStatus.newBuilder()
                    .apply {
                        code = 1234
                        message = "Expected error"
                    }
                    .build()
            }
            .build()

        // Coroutines 이므로 inline 형태의 assertFails를 사용합니다.
        assertFailsWith(StatusException::class) {
            client.unaryCall(request)
        }
    }

    @Test
    fun `streaming output call`(output: OutputCapturer) = runSuspendTest {
        val requests = getStreamingOutputCallRequests(times = 1).first()
        val responses = client.streamingOutputCall(requests)

        val responseCount = LongAdder()
        responses
            .buffer()
            .collect { response ->
                log.debug { "Response. $response" }
                responseCount.increment()
            }

        with(output.capture()) {
            this shouldContain "Response. payload {"
        }
        responseCount.toLong() shouldBeEqualTo 4L
    }

    @Test
    fun `streaming input call`(output: OutputCapturer) = runSuspendTest {
        val requests = getStreamingInputCallRequests()
        val response = client.stremingInputCall(requests)

        log.debug { "response = $response" }

        with(output.capture()) {
            this shouldContain "response = aggregated_payload_size: 51200"
        }
    }

    @Test
    fun `full duplex streaming output call`(output: OutputCapturer) = runSuspendTest {
        val requests = getStreamingOutputCallRequests()
        val responses = client.fullDuplexCall(requests)

        responses
            .buffer()
            .collect { response ->
                log.debug { "Receive response. $response" }
            }

        with(output.capture()) {
            this shouldContain "Receive response. payload {"
            this shouldContain "body: "
        }
    }

    @Test
    fun `half duplex streaming output call`(output: OutputCapturer) = runSuspendTest {
        val requests = getStreamingOutputCallRequests()
        val responses = client.halfDuplexCall(requests)

        responses
            .buffer()
            .collect { response ->
                log.debug { "Receive response. $response" }
            }

        with(output.capture()) {
            this shouldContain "Receive response. payload {"
            this shouldContain "body: "
        }
    }

    private fun getStreamingInputCallRequests(size: Int = 100, delayMs: Long = 10L) = flow {
        repeat(size) {
            val request = StreamingInputCallRequest.newBuilder()
                .setExpectCompressed(BoolValue.newBuilder().setValue(true))
                .setPayload(Payload.newBuilder().setBody(ByteString.copyFrom(randomString(512).toUtf8Bytes())))
                .build()

            emit(request)
            log.debug { "Sent request. ${request.payload.body.size()}" }
            delay(delayMs)
        }
    }

    private fun getStreamingOutputCallRequests(times: Int = 100, delayMs: Long = 10L) = flow {
        repeat(times) {
            val request = Messages.StreamingOutputCallRequest.newBuilder()
                .addResponseParameters(ResponseParameters.newBuilder().setIntervalUs(3000).setSize(128))
                .addResponseParameters(ResponseParameters.newBuilder().setIntervalUs(3000).setSize(128))
                .addResponseParameters(ResponseParameters.newBuilder().setIntervalUs(3000).setSize(128))
                .addResponseParameters(ResponseParameters.newBuilder().setIntervalUs(3000).setSize(128))
                .setPayload(
                    Payload.newBuilder().setBody(ByteString.copyFrom(randomString(1024 / 4).toUtf8Bytes()))
                )
                .build()

            emit(request)
            log.debug { "Sent request. ${request.payload.body.size()}" }
            delay(delayMs)
        }
    }
}
