package io.bluetape4k.io.jackson.text.yaml

import io.bluetape4k.io.jackson.text.AbstractJacksonTextTest
import io.bluetape4k.logging.KLogging

abstract class AbstractYamlExample: AbstractJacksonTextTest() {

    companion object: KLogging()

    protected val yamlMapper by lazy { JacksonYaml.defaultYamlMapper }
    protected val yamlFactory by lazy { JacksonYaml.defaultYamlFactory }
    protected val objectMapper by lazy { JacksonYaml.defaultObjectMapper }
}
