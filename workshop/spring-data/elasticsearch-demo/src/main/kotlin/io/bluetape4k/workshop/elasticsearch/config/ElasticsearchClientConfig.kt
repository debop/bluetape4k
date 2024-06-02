package io.bluetape4k.workshop.elasticsearch.config

import io.bluetape4k.workshop.elasticsearch.ElasticsearchApplication.Companion.elasticsearch
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration

@Configuration
class ElasticsearchClientConfig: ElasticsearchConfiguration() {

    override fun clientConfiguration(): ClientConfiguration {
        return ClientConfiguration.builder()
            .connectedTo(elasticsearch.url)
            .usingSsl(elasticsearch.createSslContextFromCa())
            .withBasicAuth("elastic", elasticsearch.password)
            .build()
    }
}
