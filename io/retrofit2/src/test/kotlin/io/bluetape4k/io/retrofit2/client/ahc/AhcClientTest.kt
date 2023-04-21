package io.bluetape4k.io.retrofit2.client.ahc

import io.bluetape4k.io.retrofit2.client.AbstractClientTest
import io.bluetape4k.io.retrofit2.clients.ahc.asyncHttpClientCallFactoryOf
import io.bluetape4k.logging.KLogging
import okhttp3.Call

class AhcClientTest: AbstractClientTest() {

    companion object: KLogging()

    override val callFactory: Call.Factory = asyncHttpClientCallFactoryOf()

}
