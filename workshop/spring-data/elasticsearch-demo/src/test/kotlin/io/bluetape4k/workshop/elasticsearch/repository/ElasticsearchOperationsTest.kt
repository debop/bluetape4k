package io.bluetape4k.workshop.elasticsearch.repository

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.elasticsearch.AbstractElasticsearchTest
import io.bluetape4k.workshop.elasticsearch.model.Conference
import org.amshove.kluent.shouldBeGreaterOrEqualTo
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.geo.GeoPoint
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery

class ElasticsearchOperationsTest @Autowired constructor(
    private val operations: ElasticsearchOperations,
    private val repository: ConferenceRepository,
): AbstractElasticsearchTest() {

    companion object: KLogging()

    @Test
    fun `search all conference`() {
        val conferences = repository.findAll().toList()

        conferences.size shouldBeGreaterOrEqualTo 5
        conferences.forEach {
            log.debug { "Conference=$it" }
        }
    }

    @Test
    fun `search by expected date and keyword`() {
        val expectedDate = "2014-10-29"
        val expectedWord = "java"

        val query = CriteriaQuery(
            Criteria(Conference::keywords.name).contains(expectedWord)
                .and(Criteria(Conference::date.name).greaterThanEqual(expectedDate))
        )

        val hits = operations.search(query, Conference::class.java)

        hits shouldHaveSize 3
        hits.map { it.content }.forEach { conference ->
            conference.keywords shouldContain expectedWord
            format.parse(conference.date).time shouldBeGreaterOrEqualTo format.parse(expectedDate).time
        }
    }

    @Test
    fun `search by geo spatial`() {
        val startLocation = GeoPoint(50.0646501, 19.9449799)
        val range = "530km" // 300mi
        val query = CriteriaQuery(Criteria(Conference::location.name).within(startLocation, range))

        val hits = operations.search(query, Conference::class.java)

        hits shouldHaveSize 2
        hits.map { it.content }.forEach { conference ->
            log.debug { "Conference=$conference" }
        }
    }
}
