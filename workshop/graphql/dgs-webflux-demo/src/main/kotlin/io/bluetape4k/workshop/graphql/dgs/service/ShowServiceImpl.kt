package io.bluetape4k.workshop.graphql.dgs.service

import io.bluetape4k.collections.eclipse.fastListOf
import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.graphql.dgs.generated.types.Show
import kotlinx.coroutines.delay
import org.springframework.stereotype.Service

@Service
class ShowServiceImpl: ShowService {

    companion object: KLogging() {
        val shows = fastListOf(
            Show(id = 1, title = "Stranger Things", releaseYear = 2016),
            Show(id = 2, title = "Ozark", releaseYear = 2017),
            Show(id = 3, title = "The Crown", releaseYear = 2016),
            Show(id = 4, title = "Dead to Me", releaseYear = 2019),
            Show(id = 5, title = "Orange is the New Black", releaseYear = 2013)
        )
    }

    override suspend fun shows(): List<Show> {
        delay(100L)
        return shows
    }

    override suspend fun shows(titleFilter: String): List<Show> {
        delay(100L)
        return shows.filter { it.title?.contains(titleFilter) ?: false }
    }
}
