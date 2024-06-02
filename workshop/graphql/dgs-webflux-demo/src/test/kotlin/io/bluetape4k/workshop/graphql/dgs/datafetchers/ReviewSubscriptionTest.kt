package io.bluetape4k.workshop.graphql.dgs.datafetchers

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest
import graphql.ExecutionResult
import graphql.GraphQLError
import io.bluetape4k.json.jackson.Jackson
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.error
import io.bluetape4k.logging.info
import io.bluetape4k.workshop.graphql.dgs.AbstractDgsTest
import io.bluetape4k.workshop.graphql.dgs.generated.client.AddReviewGraphQLQuery
import io.bluetape4k.workshop.graphql.dgs.generated.client.AddReviewProjectionRoot
import io.bluetape4k.workshop.graphql.dgs.generated.types.Review
import io.bluetape4k.workshop.graphql.dgs.generated.types.SubmittedReview
import net.datafaker.Faker
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.junit.jupiter.api.Test
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.util.concurrent.CopyOnWriteArrayList

class ReviewSubscriptionTest(
    @Autowired private val dgsQueryExecutor: DgsQueryExecutor,
): AbstractDgsTest() {

    companion object: KLogging() {
        private val faker = Faker()
        private val jsonMapper = Jackson.defaultJsonMapper
    }

    @Test
    fun `review subscription`() {
        // NOTE: Subscription 을 제공하는 서버 함수는 `suspend` 이면 안됩니다. 동기 함수여야 합니다.
        val executionResult = dgsQueryExecutor.execute(
            """
            subscription {
                reviewAdded(showId: 1) {
                    username
                    starScore
                }
            }
            """
        )
        val reviewPublisher: Publisher<ExecutionResult> = executionResult.getData()
        val reviewAddeds = CopyOnWriteArrayList<Review>()

        reviewPublisher.subscribe(object: Subscriber<ExecutionResult> {
            override fun onSubscribe(s: Subscription) {
                s.request(2)
            }

            override fun onError(t: Throwable) {
                log.error(t) { "Fail to add new review." }
            }

            override fun onComplete() {
                log.info { "Finish to subscription" }
            }

            override fun onNext(executionResult: ExecutionResult) {
                if (executionResult.errors.isNotEmpty()) {
                    log.error { "Fail to add new review. ${executionResult.errors}" }
                }
                log.debug { "subscribe onNext=$executionResult" }
                val data = executionResult.getData<Map<String, Any?>>()

                // NOTE: 여기서 Json Mapping 예외가 발생하면, 예외도 발생 안하고, 무시된다. 왜 ???
                try {
                    val review = jsonMapper.convertValue(data["reviewAdded"], Review::class.java)
                    log.info { "subscription reviewAdded=$review" }
                    reviewAddeds.add(review)
                } catch (e: Throwable) {
                    log.error(e) { "Fail to convert reviewAdded to Review" }
                }
            }
        })

        addReview()
        Thread.sleep(10)

        addReview()
        Thread.sleep(10)

        await atMost Duration.ofSeconds(3) until {
            reviewAddeds.size == 2
        }

        reviewAddeds.forEach {
            log.debug { "Subscription review=$it" }
        }
    }

    private fun addReview() {
        val request = GraphQLQueryRequest(
            AddReviewGraphQLQuery.newRequest()
                .review(SubmittedReview(1, faker.internet().username(), faker.number().numberBetween(1, 5)))
                .build(),
            AddReviewProjectionRoot().username().starScore(),
        )
        val result = dgsQueryExecutor.execute(request.serialize())

        if (result.errors.isNotEmpty()) {
            val error: GraphQLError = result.errors.first()!!
            log.error { "Fail to add new review. ${error.message}" }
        }
    }
}
