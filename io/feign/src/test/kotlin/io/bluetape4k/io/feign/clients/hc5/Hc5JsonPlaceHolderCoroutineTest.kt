package io.bluetape4k.io.feign.clients.hc5

import feign.Logger
import feign.hc5.AsyncApacheHttp5Client
import feign.kotlin.CoroutineFeign
import feign.slf4j.Slf4jLogger
import io.bluetape4k.io.feign.clients.AbstractJsonPlaceHolderCoroutineTest
import io.bluetape4k.io.feign.codec.JacksonDecoder2
import io.bluetape4k.io.feign.codec.JacksonEncoder2
import io.bluetape4k.io.feign.coroutines.coroutineFeignBuilder

class Hc5JsonPlaceHolderCoroutineTest : AbstractJsonPlaceHolderCoroutineTest() {

    override fun newBuilder(): CoroutineFeign.CoroutineBuilder<*> {
        return coroutineFeignBuilder {
            client(AsyncApacheHttp5Client())
            encoder(JacksonEncoder2())
            decoder(JacksonDecoder2())
            logger(Slf4jLogger(Hc5JsonPlaceHolderCoroutineTest::class.java))
            logLevel(Logger.Level.FULL)
        }
    }
}
