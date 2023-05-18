package io.bluetape4k.workshop.graphql.dgs.datafetchers

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsSubscription
import com.netflix.graphql.dgs.InputArgument
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.graphql.dgs.dataloader.ReviewDataLoader
import io.bluetape4k.workshop.graphql.dgs.generated.DgsConstants
import io.bluetape4k.workshop.graphql.dgs.generated.types.Review
import io.bluetape4k.workshop.graphql.dgs.generated.types.Show
import io.bluetape4k.workshop.graphql.dgs.generated.types.SubmittedReview
import io.bluetape4k.workshop.graphql.dgs.services.ReviewService
import org.reactivestreams.Publisher
import java.util.concurrent.CompletableFuture

@DgsComponent
class ReviewDataFetcher(private val reviewService: ReviewService) {

    companion object: KLogging()

    /**
     * This datafetcher will be called to resolve the "reviews" field on a Show.
     * It's invoked for each individual Show, so if we would load 10 shows, this method gets called 10 times.
     * To avoid the N+1 problem this datafetcher uses a DataLoader.
     * Although the DataLoader is called for each individual show ID, it will batch up the actual loading to a single method call to the "load" method in the ReviewsDataLoader.
     * For this to work correctly, the datafetcher needs to return a CompletableFuture.
     *
     * [Show.reviews] 조회 시, LAZY 방식처러 비동기 방식으로 [CompletableFuture]를 반환하도록 합니다.
     */
    @DgsData(parentType = DgsConstants.SHOW.TYPE_NAME, field = DgsConstants.SHOW.Reviews)
    fun reviews(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Review>> {
        // Instead of loading a DataLoader by name,
        // we can use the DgsDataFetchingEnvironment and pass in the DataLoader classname.
        val reviewDataLoader = dfe.getDataLoader<Int, List<Review>>(ReviewDataLoader::class.java)
        val show = dfe.getSource<Show>()

        log.debug { "Load reviews by showId: ${show.id}" }
        return reviewDataLoader.load(show.id)
    }

    @DgsMutation
    fun addReview(@InputArgument review: SubmittedReview): List<Review> {
        reviewService.saveReview(review)
        return reviewService.reviewsForShow(review.showId)
    }

    /**
     * NOTE: Subscription 을 제공하는 함수는 `suspend` 이면 안됩니다. 동기 함수여야 합니다.
     */
    @DgsSubscription
    fun reviewAdded(@InputArgument showId: String): Publisher<Review> {
        return reviewService.getReviewsPublisher()
    }
}
