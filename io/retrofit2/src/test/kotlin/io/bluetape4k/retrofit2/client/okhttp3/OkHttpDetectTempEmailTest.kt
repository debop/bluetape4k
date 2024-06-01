package io.bluetape4k.retrofit2.client.okhttp3

import io.bluetape4k.http.okhttp3.okhttp3Client
import io.bluetape4k.retrofit2.client.AbstractDetectTempEmailTest
import okhttp3.Call

class OkHttpDetectTempEmailTest: AbstractDetectTempEmailTest() {

    override val callFactory: Call.Factory = okhttp3Client { }

}
