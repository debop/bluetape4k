package io.bluetape4k.workshop.graphql.dgs.datafetchers

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.DgsSubscription
import com.netflix.graphql.dgs.InputArgument
import io.bluetape4k.concurrent.map
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.info
import io.bluetape4k.support.asInt
import io.bluetape4k.workshop.graphql.dgs.dataloaders.ReviewDataLoaderWithContext
import io.bluetape4k.workshop.graphql.dgs.generated.DgsConstants
import io.bluetape4k.workshop.graphql.dgs.generated.types.Review
import io.bluetape4k.workshop.graphql.dgs.generated.types.Show
import io.bluetape4k.workshop.graphql.dgs.generated.types.SubmittedReview
import io.bluetape4k.workshop.graphql.dgs.scalars.DateRange
import io.bluetape4k.workshop.graphql.dgs.service.ReviewService
import org.dataloader.DataLoader
import org.reactivestreams.Publisher
import java.util.concurrent.CompletableFuture

@DgsComponent
class ReviewDataFetcher(private val reviewService: ReviewService) {

    companion object: KLogging()

    /**
     * `@DgsData`용 함수를 비동기로 실행할 때, `CompletableFuture` 를 반환하는 것은 잘 작동하는데,
     * `suspend` 함수를 사용하면, `DataFetcher` 가 작동하지 않는다.
     */
    @DgsData(parentType = DgsConstants.SHOW.TYPE_NAME, field = DgsConstants.SHOW.Reviews)
    fun reviews(
        env: DgsDataFetchingEnvironment,
        @InputArgument minScore: Int?,
    ): CompletableFuture<List<Review>> {
        log.info { "reviews in minScore: $minScore" }

        //Instead of loading a DataLoader by name,
        // we can use the DgsDataFetchingEnvironment and pass in the DataLoader classname.
        val reviewDataLoader: DataLoader<Int, List<Review>> = env.getDataLoader(ReviewDataLoaderWithContext::class.java)

        // Because the reviews field is on Show, the getSource() method will return the Show instance.
        val show: Show = env.getSource()

        // Filter 을 모두 Load 후에 수행하므로 비효율적이다. 이 걸 어떻게 해결하나?
        /**
         * TODO: DataLoader 의 key 를 show.id 를 받는게 아닌, filter 정보도 묶어서 보낼 수 있도록 만들면 된다.
         * TODO: DataLoader<ReviewQuery, List<Review>> 로 만들면 됨
         * ```
         * interface ReviewQuery {
         *     val showId: Int
         *     val filter: (Review) -> Boolean   // 또는 minScore 를 넣는다 (DB작업 시에는 이게 낫다) JPA Query 를 하던가
         * }
         * ```
         */
        return when (minScore) {
            null -> reviewDataLoader.load(show.id)
            else -> reviewDataLoader.load(show.id)
                .map {
                    it.filter { review -> review.starScore.asInt(0) > minScore }
                }
        }
    }

    @DgsQuery
    suspend fun reviews(@InputArgument dateRange: DateRange): List<Review> {
        return reviewService.listReviews(dateRange)
    }

    @DgsMutation
    suspend fun addReview(
        @InputArgument review: SubmittedReview,
    ): List<Review> {
        reviewService.saveReview(review)
        return reviewService.reviewsForShow(review.showId).apply {
            log.debug { "all reviews for show[${review.showId}]: $this" }
        }
    }

    @DgsMutation
    suspend fun addReviews(
        @InputArgument(name = "reviews", collectionType = SubmittedReview::class) reviewsInput: List<SubmittedReview>,
    ): List<Review> {
        reviewService.saveReviews(reviewsInput)

        val showIds = reviewsInput.map { it.showId }
        val reviews = reviewService.reviewsForShows(showIds)

        return reviews.flatMap { it.value }.apply {
            log.debug { "all reviews for shows: showIds=$showIds, reviews=$this" }
        }
    }

    /**
     * NOTE: Subscription 을 제공하는 함수는 `suspend` 이면 안됩니다. 동기 함수여야 합니다.
     */
    @DgsSubscription
    fun reviewAdded(@InputArgument showId: String): Publisher<Review> {
        return reviewService.getReviewsPerShow()
    }
}
