package io.bluetape4k.openai.clients.retrofit2.utils

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.bluetape4k.openai.api.exceptions.OpenAIError
import io.bluetape4k.openai.api.exceptions.OpenAIHttpException
import io.bluetape4k.support.closeSafe
import io.reactivex.FlowableEmitter
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.BufferedReader

class ResponseBodyCallback(
    private val emitter: FlowableEmitter<SSE>,
    private var emitDone: Boolean,
    private val mapper: JsonMapper,
): Callback<ResponseBody> {

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        var reader: BufferedReader? = null

        try {
            if (!response.isSuccessful) {
                val ex = HttpException(response)
                val errorBody = response.errorBody()

                if (errorBody == null) {
                    throw ex
                } else {
                    val error = mapper.readValue<OpenAIError>(errorBody.string())
                    // TODO: openai-java 를 기반으로 다시 작성하자 (status, param, type 등을 고려해서)
                    throw OpenAIHttpException(error.toString(), ex)
                }
            }

            val input = response.body()!!.byteStream()
            reader = response.body()?.byteStream()?.reader(Charsets.UTF_8)?.buffered()
            if (reader == null) {
                return
            }
            var line: String = ""
            var sse: SSE? = null
            while (!emitter.isCancelled && (reader.readLine()?.let { line = it }) != null) {
                when {
                    line.startsWith("data:") -> {
                        val data = line.substring(5).trim()
                        sse = SSE(data)
                    }

                    line == "" && sse != null -> {
                        if (sse.isDone) {
                            if (emitDone) {
                                emitter.onNext(sse)
                            }
                            break
                        }
                        emitter.onNext(sse)
                        sse = null
                    }

                    else -> throw SSEInvalidFormatException("Invalid SSE format! $line")
                }
            }
            emitter.onComplete()

        } catch (e: Throwable) {
            onFailure(call, e)
        } finally {
            reader?.closeSafe()
        }
    }

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        emitter.onError(t)
    }
}
