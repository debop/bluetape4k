package io.bluetape4k.micrometer.instrument.retrofit2

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import okhttp3.Request
import org.apache.commons.lang3.time.StopWatch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MeasuredCall<T: Any> internal constructor(
    private val wrappedCall: Call<T>,
    private val metrics: RetrofitCallMetricsCollector,
): Call<T> by wrappedCall {

    companion object: KLogging()

    /**
     * Synchronously send the request and return its response.
     *
     * @throws IOException if a problem occurred talking to the server.
     * @throws RuntimeException (and subclasses) if an unexpected error occurs creating the request or
     * decoding the response.
     */
    override fun execute(): Response<T> {
        log.trace { "Execute call ... wrappedCall=$wrappedCall" }

        val stopwatch = StopWatch.createStarted()
        val request = wrappedCall.request()
        try {
            val response = wrappedCall.execute()
            metrics.measureRequestDuration(stopwatch.time, request, response, false)
            return response
        } catch (e: Exception) {
            metrics.measureRequestException(stopwatch.time, request, e, false)
            throw e
        }
    }

    /**
     * Asynchronously send the request and notify `callback` of its response or if an error
     * occurred talking to the server, creating the request, or processing the response.
     */
    override fun enqueue(callback: Callback<T>) {
        log.trace { "Enqueue call ... wrappedCall=$wrappedCall" }
        wrappedCall.enqueue(measuredCallback(wrappedCall.request(), callback))
    }

    private fun measuredCallback(request: Request, callback: Callback<T>): Callback<T> =
        object: Callback<T> {
            val stopwatch = StopWatch.createStarted()
            override fun onResponse(call: Call<T>, response: Response<T>) {
                metrics.measureRequestDuration(stopwatch.time, request, response, true)
                callback.onResponse(call, response)
            }

            override fun onFailure(call: Call<T>, error: Throwable) {
                metrics.measureRequestException(stopwatch.time, request, error, true)
                callback.onFailure(call, error)
            }
        }
}
