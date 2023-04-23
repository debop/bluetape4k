package io.bluetape4k.io.retrofit2.client.okhttp3

import io.bluetape4k.io.http.okhttp3.okhttp3Client
import io.bluetape4k.io.retrofit2.client.AbstractJsonPlaceHolderSyncTest
import okhttp3.Call

class OkHttpJsonPlaceHolderSyncTest: AbstractJsonPlaceHolderSyncTest() {

    override val callFactory: Call.Factory = okhttp3Client { }

}
