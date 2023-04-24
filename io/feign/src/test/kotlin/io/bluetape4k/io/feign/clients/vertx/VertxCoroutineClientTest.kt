package io.bluetape4k.io.feign.clients.vertx

import feign.Logger
import feign.kotlin.CoroutineFeign
import feign.slf4j.Slf4jLogger
import io.bluetape4k.io.feign.clients.AbstractCoroutineClientTest
import io.bluetape4k.io.feign.coroutines.coroutineFeignBuilder
import io.bluetape4k.logging.KLogging

class VertxCoroutineClientTest: AbstractCoroutineClientTest() {

    companion object: KLogging()

    override fun newCoroutineBuilder(): CoroutineFeign.CoroutineBuilder<*> {
        return coroutineFeignBuilder {
            client(AsyncVertxHttpClient())
            logger(Slf4jLogger(VertxCoroutineClientTest::class.java))
            logLevel(Logger.Level.FULL)
        }
    }
}
