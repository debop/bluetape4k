package io.bluetape4k.io.http.hc5.examples

import io.bluetape4k.io.http.hc5.AbstractHc5Test
import io.bluetape4k.io.http.hc5.entity.toStringOrNull
import io.bluetape4k.io.http.hc5.httpClientOf
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.core5.http.message.StatusLine
import org.junit.jupiter.api.Test

class ClientResponseProcessing: AbstractHc5Test() {

    companion object: KLogging()

    @Test
    fun `get response examples`() {
        httpClientOf().use { httpclient ->
            val httpget = HttpGet("$httpbinBaseUrl/get")

            log.debug { "Executing request ${httpget.method} ${httpget.uri}" }

            val result = httpclient.execute(httpget) { response ->
                log.debug { "----------------" }
                log.debug { "$httpget -> " + StatusLine(response) }
                Result(response.code, response.entity.toStringOrNull())
            }
            log.debug { result }
        }
    }

    data class Result(val status: Int, val content: String?)
}
