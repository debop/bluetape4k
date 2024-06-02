package io.bluetape4k.workshop.elasticsearch

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.uninitialized
import io.bluetape4k.testcontainers.storage.ElasticsearchServer
import io.bluetape4k.workshop.elasticsearch.model.Conference
import io.bluetape4k.workshop.elasticsearch.repository.ConferenceRepository
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.geo.GeoPoint
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories

@SpringBootApplication
@EnableElasticsearchRepositories
class ElasticsearchApplication {

    companion object: KLogging() {
        // Elasticsearch Server
        val elasticsearch = ElasticsearchServer.Launcher.elasticsearch
    }

    @Autowired
    private val operations: ElasticsearchOperations = uninitialized()

    @Autowired
    private val repository: ConferenceRepository = uninitialized()

    @PostConstruct
    fun insertSampleData() {
        operations.indexOps(Conference::class.java).refresh()

        // Save sample data
        val documents = listOf(
            Conference(
                date = "2014-11-06",
                name = "Spring eXchange 2014 - London",
                keywords = mutableListOf("java", "spring"),
                location = GeoPoint(51.500152, -0.126236),
            ),
            Conference(
                date = "2014-12-07",
                name = "Scala eXchange 2014 - London",
                keywords = mutableListOf("scala", "play", "java"),
                location = GeoPoint(51.500152, -0.126236),
            ),
            Conference(
                date = "2014-11-20",
                name = "Elasticsearch 2014 - Berlin",
                keywords = mutableListOf("java", "elasticsearch", "kibana"),
                location = GeoPoint(52.5234051, 13.4113999),
            ),
            Conference(
                date = "2014-11-12",
                name = "AWS London 2014",
                keywords = mutableListOf("cloud", "aws"),
                location = GeoPoint(51.500152, -0.126236),
            ),
            Conference(
                date = "2014-10-04",
                name = "JDD14 - Cracow",
                keywords = mutableListOf("java", "spring"),
                location = GeoPoint(50.0646501, 19.9449799),
            )
        )

        repository.saveAll(documents)
    }
}


fun main(vararg args: String) {
    runApplication<ElasticsearchApplication>(*args)
}
