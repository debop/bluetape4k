package io.bluetape4k.workshop.elasticsearch.repository

import io.bluetape4k.workshop.elasticsearch.model.Conference
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface ConferenceRepository: ElasticsearchRepository<Conference, String> {

    fun findByName(name: String): List<Conference>

}
