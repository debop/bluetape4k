package io.bluetape4k.io.retrofit2.client.vertx

import io.bluetape4k.io.retrofit2.client.AbstractClientTest
import io.bluetape4k.io.retrofit2.clients.vertx.vertxCallFactoryOf
import io.bluetape4k.logging.KLogging
import okhttp3.Call

class VertxHttpClientTest: AbstractClientTest() {

    companion object: KLogging()

    override val callFactory: Call.Factory = vertxCallFactoryOf()

}
