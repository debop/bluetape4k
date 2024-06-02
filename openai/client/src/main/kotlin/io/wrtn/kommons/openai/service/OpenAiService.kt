package io.bluetape4k.openai.service

import io.bluetape4k.logging.KLogging
import io.bluetape4k.openai.client.api.OpenAiApi
import java.io.Closeable
import java.util.concurrent.ExecutorService
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class OpenAiService private constructor(
    private val api: OpenAiApi,
    private val executorService: ExecutorService?,
): Closeable {

    companion object: KLogging() {

        private const val API_URL = "https://api.openai.com"
        private val DEFAULT_TIMEOUT = 10.seconds

        @Suppress("UNUSED_PARAMETER")
        operator fun invoke(
            apiToken: String,
            baseUrl: String = API_URL,
            timeout: Duration = DEFAULT_TIMEOUT,
        ): OpenAiService {
            TODO("구현 중")
        }
    }


    override fun close() {
        executorService?.shutdown()
    }
}
