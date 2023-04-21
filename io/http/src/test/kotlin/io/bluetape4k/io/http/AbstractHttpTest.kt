package io.bluetape4k.io.http

import io.bluetape4k.logging.KLogging
import org.junit.jupiter.api.fail

abstract class AbstractHttpTest {

    companion object: KLogging() {
        const val HELLOWORLD_URL = "http://publicobject.com/helloworld.txt"
        const val HTTPBIN_URL = "https://httpbin.org"
        const val JSON_PLACEHOLDER_URL = "https://jsonplaceholder.typicode.com"
        const val JSON_PLACEHOLDER_TODOS_URL = "$JSON_PLACEHOLDER_URL/todos"
    }

    fun assertResponse(okResponse: okhttp3.Response?) {
        if (okResponse == null) {
            fail { "Response is null" }
        }
        if (!okResponse.isSuccessful) {
            fail { "Unexpected code ${okResponse.code}" }
        }
    }
}
