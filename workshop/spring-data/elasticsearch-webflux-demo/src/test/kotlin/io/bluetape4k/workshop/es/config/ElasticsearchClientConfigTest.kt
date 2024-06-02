package io.bluetape4k.workshop.es.config

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.uninitialized
import io.bluetape4k.workshop.es.AbstractEsWebfluxDemoTest
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate

class ElasticsearchClientConfigTest: AbstractEsWebfluxDemoTest() {

    companion object: KLogging()

    @Autowired
    private val reactiveOperations: ReactiveElasticsearchTemplate = uninitialized()

    @Test
    fun `context loading`() {
        operations.shouldNotBeNull()
        reactiveOperations.shouldNotBeNull()

        log.debug { "runtime version=${operations.runtimeLibraryVersion}" }

    }
}
