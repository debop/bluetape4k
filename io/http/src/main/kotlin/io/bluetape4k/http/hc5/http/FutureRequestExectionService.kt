package io.bluetape4k.http.hc5.http

import org.apache.hc.client5.http.classic.HttpClient
import org.apache.hc.client5.http.impl.classic.FutureRequestExecutionService
import java.util.concurrent.ExecutorService

fun futureRequestExecutionServiceOf(
    httpclient: HttpClient,
    executor: ExecutorService,
): FutureRequestExecutionService {
    return FutureRequestExecutionService(httpclient, executor)
}
