package io.bluetape4k.workshop.graphql.dgs.datafetchers

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest
import com.ninjasquad.springmockk.MockkBean
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.support.uninitialized
import io.bluetape4k.workshop.graphql.dgs.AbstractDgsTest
import io.bluetape4k.workshop.graphql.dgs.generated.client.ShowsGraphQLQuery
import io.bluetape4k.workshop.graphql.dgs.generated.client.ShowsProjectionRoot
import io.bluetape4k.workshop.graphql.dgs.generated.types.Review
import io.bluetape4k.workshop.graphql.dgs.generated.types.Show
import io.bluetape4k.workshop.graphql.dgs.service.ReviewService
import io.bluetape4k.workshop.graphql.dgs.service.ShowService
import io.mockk.clearAllMocks
import io.mockk.coEvery
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant


class ShowDataFetcherTest(
    @Autowired private val dgsQueryExecutor: DgsQueryExecutor,
): AbstractDgsTest() {

    @MockkBean(relaxed = true)
    private val showService: ShowService = uninitialized()

    @MockkBean(relaxed = true)
    private val reviewService: ReviewService = uninitialized()

    private lateinit var expectedTitle: String

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
        expectedTitle = faker.book().title()

        coEvery { showService.shows() } returns listOf(Show(1, expectedTitle, 2020))
        coEvery { reviewService.reviewsForShows(setOf(1)) } returns mapOf(
            1 to listOf(
                Review("DGS User 1", 5, Instant.now().minusSeconds(120_00L)),
                Review("DGS User 2", 3, Instant.now().minusSeconds(60_000L))
            )
        )
    }

    @Test
    fun `get shows with title and releaseYear`() = runSuspendWithIO {
        val titles = dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
            """
                {
                    shows {
                        title
                        releaseYear
                    }
                }
            """.trimIndent(),
            "data.shows[*].title"
        )
        titles shouldBeEqualTo listOf(expectedTitle)
    }


    @Test
    fun `raise exception when get shows`() = runSuspendWithIO {
        coEvery { showService.shows() } throws RuntimeException("Nothing to see here")
        val result = dgsQueryExecutor.execute(
            """
                {
                    shows {
                        title
                        releaseYear
                    }
                }
            """.trimIndent(),
        )
        result.errors.shouldNotBeEmpty()
        result.errors.first().message shouldContain "Nothing to see here"
    }

    @Test
    fun `get shows by client query api`() = runSuspendWithIO {
        val queryRequest = GraphQLQueryRequest(
            ShowsGraphQLQuery.newRequest().titleFilter("").build(),
            ShowsProjectionRoot().title()
        )
        val titles = dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
            queryRequest.serialize(),
            "data.shows[*].title"
        )
        titles shouldBeEqualTo listOf(expectedTitle)
    }
}
