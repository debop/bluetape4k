package io.bluetape4k.retrofit2.client.ahc

import io.bluetape4k.retrofit2.client.AbstractDynamicUrlCoroutineTest
import io.bluetape4k.retrofit2.clients.ahc.asyncHttpClientCallFactoryOf
import okhttp3.Call

class AhcDynamicUrlCoroutineTest: AbstractDynamicUrlCoroutineTest() {

    override val callFactory: Call.Factory = asyncHttpClientCallFactoryOf()

}
