package io.bluetape4k.workshop.es

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.storage.ElasticsearchServer
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.elasticsearch.config.EnableReactiveElasticsearchAuditing
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories

@SpringBootApplication
@EnableReactiveElasticsearchRepositories
@EnableReactiveElasticsearchAuditing
class EsDemoApplication {
    companion object: KLogging() {
        val esServer = ElasticsearchServer.Launcher.elasticsearch
    }
}

fun main(vararg args: String) {
    runApplication<EsDemoApplication>(*args) {
        webApplicationType = WebApplicationType.REACTIVE
    }
}
