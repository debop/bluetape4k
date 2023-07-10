package io.nats.examples

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.nats.AbstractNatsTest
import io.bluetape4k.nats.client.publish
import io.bluetape4k.support.toUtf8String
import io.nats.client.impl.Headers
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch

class ScatterGather: AbstractNatsTest() {

    companion object: KLogging()

    @Test
    fun `scatter and gather`() {
        getConnection().use { requesterConn ->
            getConnection().use { responderConn1 ->
                getConnection().use { responderConn2 ->
                    val d1 = responderConn1.createDispatcher { msg ->
                        log.debug { "Responder A replying to request ${msg.data.toUtf8String()} via subject `${msg.subject}`" }
                        val headers = Headers().put("responderId", "A")
                        responderConn1.publish(msg.replyTo, headers, msg.data)
                    }
                    d1.subscribe("scatter")

                    val d2 = responderConn2.createDispatcher { msg ->
                        log.debug { "Responder B replying to request ${msg.data.toUtf8String()} via subject `${msg.subject}`" }
                        val headers = Headers().put("responderId", "B")
                        responderConn2.publish(msg.replyTo, headers, msg.data)
                    }
                    d2.subscribe("scatter")

                    val latch = CountDownLatch(10)
                    val d = requesterConn.createDispatcher { msg ->
                        val mId = msg.data.toUtf8String()
                        val responderId = msg.headers.getFirst("responderId")
                        log.debug { "Response gathered for message $mId received from responderId $responderId." }
                        latch.countDown()
                    }
                    d.subscribe("gather")

                    repeat(10) {
                        log.debug { "Publish scatter request #$it" }
                        requesterConn.publish("scatter", "gather", it.toString())
                        Thread.sleep(100)
                    }
                    latch.await()
                }
            }
        }
    }
}
