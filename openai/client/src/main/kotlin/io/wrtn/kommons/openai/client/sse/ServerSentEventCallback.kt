package io.bluetape4k.openai.client.sse

import okhttp3.ResponseBody
import reactor.core.publisher.FluxSink
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Retrofit2 @Streaming 으로 지정된 메소드의 응답은 Stream 방식이라,
 * Server Sent FlowEvent (SSE) 으로 파싱한 후 [emitter]를 통해 전달합니다.
 */
class ServerSentEventCallback(
    val emitter: FluxSink<ServerSentEvent>,
    val emitDone: Boolean,
): Callback<ResponseBody> {

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        TODO("Not yet implemented")
    }

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        emitter.error(t)
    }
}
