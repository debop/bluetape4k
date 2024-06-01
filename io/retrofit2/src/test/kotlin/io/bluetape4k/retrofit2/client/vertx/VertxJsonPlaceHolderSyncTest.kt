package io.bluetape4k.retrofit2.client.vertx

import io.bluetape4k.retrofit2.client.AbstractJsonPlaceHolderSyncTest
import io.bluetape4k.retrofit2.clients.vertx.vertxCallFactoryOf
import okhttp3.Call

class VertxJsonPlaceHolderSyncTest: AbstractJsonPlaceHolderSyncTest() {

    override val callFactory: Call.Factory = vertxCallFactoryOf()

}
