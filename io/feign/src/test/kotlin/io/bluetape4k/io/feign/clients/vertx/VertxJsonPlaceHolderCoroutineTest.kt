package io.bluetape4k.io.feign.clients.vertx

import feign.Logger
import feign.kotlin.CoroutineFeign
import feign.slf4j.Slf4jLogger
import io.bluetape4k.io.feign.clients.AbstractJsonPlaceHolderCoroutineTest
import io.bluetape4k.io.feign.codec.JacksonDecoder2
import io.bluetape4k.io.feign.codec.JacksonEncoder2
import io.bluetape4k.io.feign.coroutines.coroutineFeignBuilder

class VertxJsonPlaceHolderCoroutineTest : AbstractJsonPlaceHolderCoroutineTest() {

    override fun newBuilder(): CoroutineFeign.CoroutineBuilder<*> {
        return coroutineFeignBuilder {
            client(AsyncVertxHttpClient())
            encoder(JacksonEncoder2())
            decoder(JacksonDecoder2())
            logger(Slf4jLogger(VertxJsonPlaceHolderCoroutineTest::class.java))
            logLevel(Logger.Level.FULL)
        }
    }
}
