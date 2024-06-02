package io.bluetape4k.workshop.graphql.dgs.datafetcher

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest
import com.netflix.graphql.dgs.client.jsonTypeRef
import com.ninjasquad.springmockk.MockkBean
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.uninitialized
import io.bluetape4k.workshop.graphql.dgs.AbstractDgsTest
import io.bluetape4k.workshop.graphql.dgs.generated.client.AddReviewGraphQLQuery
import io.bluetape4k.workshop.graphql.dgs.generated.client.AddReviewProjectionRoot
import io.bluetape4k.workshop.graphql.dgs.generated.client.ShowsGraphQLQuery
import io.bluetape4k.workshop.graphql.dgs.generated.client.ShowsProjectionRoot
import io.bluetape4k.workshop.graphql.dgs.generated.types.Review
import io.bluetape4k.workshop.graphql.dgs.generated.types.Show
import io.bluetape4k.workshop.graphql.dgs.generated.types.SubmittedReview
import io.bluetape4k.workshop.graphql.dgs.generated.types.TitleFormat
import io.bluetape4k.workshop.graphql.dgs.services.ReviewService
import io.bluetape4k.workshop.graphql.dgs.services.ShowService
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.verify
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldHaveSize
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.OffsetDateTime

class ShowDataFetcherTest(
    @Autowired private val dgsQueryExecutor: DgsQueryExecutor,
): AbstractDgsTest() {

    companion object: KLogging()

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
                Review("DGS User 1", 5, OffsetDateTime.now().minusDays(2)),
                Review("DGS User 2", 3, OffsetDateTime.now().minusDays(1))
            )
        )
    }

    @Test
    fun `context loading`() {
        dgsQueryExecutor.shouldNotBeNull()
    }

    @Test
    fun `get shows`() = runSuspendWithIO {
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
    fun `get shows with exception`() = runSuspendWithIO {

        coEvery { showService.shows() } throws RuntimeException("nothing to see here")

        val result = dgsQueryExecutor.execute(
            """
            {
                shows {
                    title
                    releaseYear
                }
            }
            """.trimIndent()
        )
        result.errors.shouldNotBeEmpty()
        result.errors.first().message shouldBeEqualTo "java.lang.RuntimeException: nothing to see here"
    }

    @Test
    fun `get shows by query api function`() {
        val queryRequest = GraphQLQueryRequest(
            ShowsGraphQLQuery.Builder().build(),
            ShowsProjectionRoot().title()
        )
        val titles = dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
            queryRequest.serialize(),
            "data.shows[*].title"
        )
        titles shouldBeEqualTo listOf(expectedTitle)
    }


    @Test
    fun `show with reviews`() {
        val queryRequest = GraphQLQueryRequest(
            ShowsGraphQLQuery.Builder().build(),
            ShowsProjectionRoot()
                .title(TitleFormat(true))
                .parent
                .reviews()
                .username()
                .starScore()
        )

        val shows: List<Show> = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            queryRequest.serialize(),
            "data.shows[*]",
            jsonTypeRef<List<Show>>()
        )
        shows shouldHaveSize 1
        shows.first().reviews.shouldNotBeNull() shouldHaveSize 2
    }

    @Test
    fun `add review mutation`() {
        val queryRequest = GraphQLQueryRequest(
            AddReviewGraphQLQuery.Builder()
                .review(SubmittedReview(1, "testuser", 5))
                .build(),
            AddReviewProjectionRoot()
                .username()
                .starScore()
        )

        val executionResult = dgsQueryExecutor.execute(queryRequest.serialize())
        executionResult.errors.shouldBeEmpty()

        verify(exactly = 1) { reviewService.reviewsForShow(1) }
    }
}
