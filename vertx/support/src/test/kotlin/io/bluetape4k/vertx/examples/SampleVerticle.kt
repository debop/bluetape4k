package io.bluetape4k.vertx.examples

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.error
import io.bluetape4k.logging.info
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise

class SampleVerticle : AbstractVerticle() {

    companion object : KLogging()

    override fun start(startPromise: Promise<Void>) {
        vertx.createHttpServer()
            .requestHandler { req ->
                log.debug { "Received request: $req" }
                req.response()
                    .putHeader("Content-Type", "text/plain")
                    .end("Yo!")
                log.info { "Handle a request on path ${req.path()} from ${req.remoteAddress().host()}" }
            }
            .listen(11981) { ar ->
                if (ar.succeeded()) {
                    log.info { "Server is now listening!" }
                    startPromise.complete()
                } else {
                    log.error(ar.cause()) { "Failed to bind!" }
                    startPromise.fail(ar.cause())
                }
            }
    }
}
