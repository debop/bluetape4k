package io.bluetape4k.io.retrofit2.client.okhttp3

import io.bluetape4k.io.http.okhttp3.okhttp3Client
import io.bluetape4k.io.retrofit2.client.AbstractClientTest
import io.bluetape4k.logging.KLogging
import okhttp3.Call

class OkHttpClientTest: AbstractClientTest() {

    companion object: KLogging()

    override val callFactory: Call.Factory = okhttp3Client { }
}
