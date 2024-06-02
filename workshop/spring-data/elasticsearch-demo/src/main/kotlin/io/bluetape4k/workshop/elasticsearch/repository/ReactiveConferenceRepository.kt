package io.bluetape4k.workshop.elasticsearch.repository

import io.bluetape4k.workshop.elasticsearch.model.Conference
import org.springframework.data.elasticsearch.core.geo.GeoPoint
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository
import reactor.core.publisher.Flux

interface ReactiveConferenceRepository: ReactiveElasticsearchRepository<Conference, String> {

    fun findAllByKeywordsContainsAndDateAfter(keyword: String, date: String): Flux<Conference>

    fun findAllByLocationNear(location: GeoPoint, distance: String): Flux<Conference>
}
