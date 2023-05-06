package io.bluetape4k.io.feign.clients.vertx

import feign.Feign
import feign.Logger
import feign.slf4j.Slf4jLogger
import io.bluetape4k.io.feign.clients.AbstractJsonPlaceHolderSyncTest
import io.bluetape4k.io.feign.codec.JacksonDecoder2
import io.bluetape4k.io.feign.codec.JacksonEncoder2
import io.bluetape4k.io.feign.feignBuilder

class VertxJsonPlaceHolderSyncTest: AbstractJsonPlaceHolderSyncTest() {

    override fun newBuilder(): Feign.Builder {
        return feignBuilder {
            client(VertxHttpClient())
            encoder(JacksonEncoder2())
            decoder(JacksonDecoder2())
            logger(Slf4jLogger(javaClass))
            logLevel(Logger.Level.FULL)
        }
    }
}
