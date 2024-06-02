package io.bluetape4k.openai.api

import io.bluetape4k.json.jackson.Jackson
import io.bluetape4k.logging.KLogging

abstract class AbstractApiTest {

    companion object: KLogging()

    protected val mapper = Jackson.defaultJsonMapper
}
