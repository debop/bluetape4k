package io.bluetape4k.workshop.es.config

import io.bluetape4k.workshop.es.EsDemoApplication.Companion.esServer
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration

@Configuration
class ElasticsearchClientConfig: ElasticsearchConfiguration() {

    override fun clientConfiguration(): ClientConfiguration {
        return ClientConfiguration.builder()
            .connectedTo(esServer.url)
            .usingSsl(esServer.createSslContextFromCa())
            .withBasicAuth("elastic", esServer.password)
            .build()
    }
}
