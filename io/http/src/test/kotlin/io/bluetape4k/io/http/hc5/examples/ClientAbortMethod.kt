package io.bluetape4k.io.http.hc5.examples

import io.bluetape4k.io.http.hc5.AbstractHc5Test
import io.bluetape4k.io.http.hc5.entity.consume
import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.debug
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import org.amshove.kluent.internal.assertFails
import org.amshove.kluent.shouldBeTrue
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.message.StatusLine
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Test

class ClientAbortMethod: AbstractHc5Test() {

    @Test
    fun `abort http method before its normal completion`() = runSuspendTest {
        val executed = atomic(false)

        HttpClients.createDefault().use { httpclient ->
            val httpget = HttpGet("$httpbinBaseUrl/get")

            val task = future {
                log.debug { "Execute request ${httpget.method} ${httpget.uri}" }
                executed.value = true

                // abort 되면 RequestFailedException 예외가 발생합니다.
                assertFails {
                    httpclient.execute(httpget) { response ->
                        log.debug { "-------------------" }
                        log.debug { "$httpget  -> ${StatusLine(response)}" }
                        response.entity?.consume()
                    }
                }
            }
            await.until { !executed.value }
            httpget.cancel().shouldBeTrue()
            task.await()
        }
    }
}
