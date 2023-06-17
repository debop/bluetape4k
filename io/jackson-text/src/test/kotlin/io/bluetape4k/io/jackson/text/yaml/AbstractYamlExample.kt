package io.bluetape4k.io.jackson.text.yaml

import io.bluetape4k.io.jackson.text.AbstractJacksonTextTest
import io.bluetape4k.logging.KLogging

abstract class AbstractYamlExample: AbstractJacksonTextTest() {

    companion object: KLogging()

    protected val yamlMapper = JacksonYaml.defaultYamlMapper
    protected val yamlFactory = JacksonYaml.defaultYamlFactory
    protected val objectMapper = JacksonYaml.defaultObjectMapper
}
