package io.bluetape4k.retrofit2.client.vertx

import io.bluetape4k.retrofit2.client.AbstractJsonPlaceHolderCoroutineTest
import io.bluetape4k.retrofit2.clients.vertx.vertxCallFactoryOf
import okhttp3.Call

class VertxJsonPlaceHolderCoroutineTest: AbstractJsonPlaceHolderCoroutineTest() {

    override val callFactory: Call.Factory = vertxCallFactoryOf()

}
