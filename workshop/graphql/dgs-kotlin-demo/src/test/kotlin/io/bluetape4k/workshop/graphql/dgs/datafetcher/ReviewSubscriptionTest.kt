package io.bluetape4k.workshop.graphql.dgs.datafetcher

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest
import com.ninjasquad.springmockk.MockkBean
import graphql.ExecutionResult
import io.bluetape4k.io.json.jackson.Jackson
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import io.bluetape4k.workshop.graphql.dgs.AbstractDgsTest
import io.bluetape4k.workshop.graphql.dgs.generated.client.AddReviewGraphQLQuery
import io.bluetape4k.workshop.graphql.dgs.generated.client.AddReviewProjectionRoot
import io.bluetape4k.workshop.graphql.dgs.generated.types.Review
import io.bluetape4k.workshop.graphql.dgs.generated.types.SubmittedReview
import io.bluetape4k.workshop.graphql.dgs.services.ShowService
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.CopyOnWriteArrayList

class ReviewSubscriptionTest(
    @Autowired private val dgsQueryExecutor: DgsQueryExecutor
): AbstractDgsTest() {

    companion object: KLogging() {
        val jsonMapper = Jackson.defaultJsonMapper
    }

    @MockkBean(relaxed = true)
    private lateinit var showsService: ShowService

    @Test
    fun `review subscription`() {
        val executionResult = dgsQueryExecutor.execute(
            "subscription { reviewAdded(showId: 1) { username, starScore, submittedDate } }"
        )
        val reviewPublisher = executionResult.getData<Publisher<ExecutionResult>>()
        val reviews = CopyOnWriteArrayList<Review>()

        reviewPublisher.subscribe(object: Subscriber<ExecutionResult> {
            override fun onSubscribe(s: Subscription) {
                s.request(2)
            }

            override fun onNext(t: ExecutionResult) {
                val data = t.getData<Map<String, Any>>()
                log.info { "onNext executionResult=$t" }
                reviews.add(jsonMapper.convertValue(data["reviewAdded"], Review::class.java))
            }

            override fun onError(t: Throwable?) {
            }

            override fun onComplete() {
            }
        })

        addReview().errors.shouldBeEmpty()
        addReview().errors.shouldBeEmpty()

        reviews shouldHaveSize 2
    }

    private fun addReview(): ExecutionResult {
        val queryRequest = GraphQLQueryRequest(
            AddReviewGraphQLQuery.Builder()
                .review(SubmittedReview(1, faker.name().username(), faker.number().numberBetween(1, 5)))
                .build(),
            AddReviewProjectionRoot()
                .username()
                .starScore()
        )

        return dgsQueryExecutor.execute(queryRequest.serialize())
    }
}
