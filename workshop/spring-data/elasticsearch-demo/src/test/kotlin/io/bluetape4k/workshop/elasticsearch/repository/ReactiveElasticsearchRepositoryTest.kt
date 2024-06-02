package io.bluetape4k.workshop.elasticsearch.repository

import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.elasticsearch.AbstractElasticsearchTest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeGreaterOrEqualTo
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.elasticsearch.core.geo.GeoPoint

@SpringBootTest
class ReactiveElasticsearchRepositoryTest(
    @Autowired private val repository: ReactiveConferenceRepository,
): AbstractElasticsearchTest() {

    @Test
    fun `search all conference`() = runTest {
        val conferences = repository.findAll().asFlow().toList()

        conferences.size shouldBeGreaterOrEqualTo 5
        conferences.forEach {
            log.debug { "Conference=$it" }
        }
    }

    @Test
    fun `search by expected keywoard and date by repository`() = runTest {
        val expectedDate = "2014-10-29"
        val expectedWord = "java"

        val result = repository.findAllByKeywordsContainsAndDateAfter(expectedWord, expectedDate).asFlow().toList()

        result shouldHaveSize 3
        result.forEach { conference ->
            conference.keywords shouldContain expectedWord
            format.parse(conference.date).time shouldBeGreaterOrEqualTo format.parse(expectedDate).time
        }
    }

    @Test
    fun `search by geo spatial`() = runTest {
        val startLocation = GeoPoint(50.0646501, 19.9449799)
        val range = "530km" // 300mi

        val hits = repository.findAllByLocationNear(startLocation, range).asFlow().toList()

        hits shouldHaveSize 2
        hits.forEach { conference ->
            log.debug { "Conference=$conference" }
        }
    }
}
