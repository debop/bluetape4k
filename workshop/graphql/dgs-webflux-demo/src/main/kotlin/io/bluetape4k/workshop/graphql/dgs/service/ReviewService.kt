package io.bluetape4k.workshop.graphql.dgs.service

import io.bluetape4k.workshop.graphql.dgs.generated.types.Review
import io.bluetape4k.workshop.graphql.dgs.generated.types.SubmittedReview
import io.bluetape4k.workshop.graphql.dgs.scalars.DateRange
import org.reactivestreams.Publisher

interface ReviewService {

    suspend fun reviewsForShow(showId: Int): List<Review>

    suspend fun reviewsForShows(showIds: Collection<Int>): Map<Int, List<Review>>

    suspend fun saveReview(reviewInput: SubmittedReview)

    suspend fun saveReviews(reviewsInput: List<SubmittedReview>)

    fun getReviewsPerShow(): Publisher<Review>

    suspend fun listReviews(dateRange: DateRange): List<Review>
}
