package io.bluetape4k.workshop.es.config

import io.bluetape4k.testcontainers.storage.ElasticsearchServer
import io.bluetape4k.workshop.es.EsApplication.Companion.esServer
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration

@Configuration
class ElasticsearchClientConfig: ElasticsearchConfiguration() {

    /**
     * 이렇게 Spring 용 ClientConfiguration을 제공해야 Repository가 제대로 동작한다.
     */
    override fun clientConfiguration(): ClientConfiguration {
        return ElasticsearchServer.Launcher.getClientConfiguration(esServer)
    }
}
