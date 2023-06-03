package io.bluetape4k.io.http.hc5

import io.bluetape4k.junit5.concurrency.MultithreadingTester
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.error
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.junit.jupiter.api.Test

class ClientMultiThreadedExecution: AbstractHc5Test() {

    companion object: KLogging()

    private val cm = httpClientConnectionManager {
        setMaxConnTotal(100)
    }

    @Test
    fun `execute get in multi threading`() {
        httpClient { setConnectionManager(cm) }.use { httpclient ->
            MultithreadingTester()
                .numThreads(4)
                .roundsPerThread(2)
                .add {
                    executeHttpGet(httpclient, urisToGet[0], 0)
                }
                .add {
                    executeHttpGet(httpclient, urisToGet[1], 1)
                }
                .add {
                    executeHttpGet(httpclient, urisToGet[2], 2)
                }
                .run()
        }
    }

    private fun executeHttpGet(httpclient: CloseableHttpClient, uriToGet: String, id: Int) {
        try {
            val httpget = HttpGet(uriToGet)
            httpclient.execute(httpget) { response ->
                log.debug { "get execute to get $uriToGet[$id]" }
                val entity = response.entity
                if (entity != null) {
                    val bytes = EntityUtils.toByteArray(entity)
                    log.debug { "$id - ${bytes.size} bytes read" }
                }
            }
        } catch (e: Exception) {
            log.error(e) { "Fail to get url. $uriToGet[$id]" }
        }
    }
}
