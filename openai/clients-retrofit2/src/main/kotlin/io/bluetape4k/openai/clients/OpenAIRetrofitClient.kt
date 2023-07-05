package io.bluetape4k.openai.clients

import com.fasterxml.jackson.databind.json.JsonMapper
import io.bluetape4k.logging.KLogging
import io.bluetape4k.openai.clients.jackson.OpenAIJackson
import java.time.Duration

class OpenAIRetrofitClient {

    companion object: KLogging() {
        private const val BASE_URL = "https://api/openai.com"
        private val DEFAULT_TIMEOUT = Duration.ofSeconds(10)
        private val mapper: JsonMapper = OpenAIJackson.defaultJsonMapper()
    }
}
