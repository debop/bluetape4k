package io.bluetape4k.workshop.graphql.dgs.service

import io.bluetape4k.collections.eclipse.fastListOf
import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.info
import io.bluetape4k.workshop.graphql.dgs.generated.types.Review
import io.bluetape4k.workshop.graphql.dgs.generated.types.SubmittedReview
import io.bluetape4k.workshop.graphql.dgs.scalars.DateRange
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.datafaker.Faker
import org.reactivestreams.Publisher
import org.springframework.stereotype.Service
import reactor.core.publisher.ConnectableFlux
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

@Service
class ReviewServiceImpl(private val showService: ShowService): ReviewService {

    companion object: KLogging() {
        private val faker = Faker()
    }

    private val showIdReviews = ConcurrentHashMap<Int, MutableList<Review>>()

    private lateinit var reviewStream: FluxSink<Review>
    private lateinit var reviewPublisher: ConnectableFlux<Review>

    @PostConstruct
    private fun createReviews() {
        val shows = runBlocking { showService.shows() }

        shows.forEach { show ->
            showIdReviews[show.id!!] = MutableList(faker.number().numberBetween(2, 20)) {
                val instant = faker.date()
                    .past(300, TimeUnit.DAYS)
                    .toInstant()

                Review(
                    username = faker.name().fullName(),
                    starScore = faker.number().numberBetween(0, 6),
                    submittedDate = instant
                )
            }
        }

        // GraphQL Subscription 으로 신규로 등록되는 [Review] 정보를 전달하기 위한 Publisher 입니다.
        // WebSocket SSE 와 유사합니다.
        log.info { "Create review publisher" }
        val publisher: Flux<Review> = Flux.create { emitter -> reviewStream = emitter }
        reviewPublisher = publisher.publish()
        reviewPublisher.connect()
    }

    override suspend fun reviewsForShow(showId: Int): List<Review> {
        log.debug { "Get reviews for show [$showId]" }
        delay(10L)
        return showIdReviews.computeIfAbsent(showId) { fastListOf() }
    }

    override suspend fun reviewsForShows(showIds: Collection<Int>): Map<Int, List<Review>> {
        log.debug { "Get reviews for shows. showIds: $showIds" }
        delay(10L)
        return showIdReviews.filter { showIds.contains(it.key) }
    }

    override suspend fun saveReview(reviewInput: SubmittedReview) {
        log.debug { "Save new review. $reviewInput" }

        val reviewsForShow: MutableList<Review> = showIdReviews.computeIfAbsent(reviewInput.showId) { fastListOf() }
        val review = Review(
            username = reviewInput.username,
            starScore = reviewInput.starScore,
            submittedDate = Instant.now()
        )
        reviewsForShow.add(review)
        reviewStream.next(review)

        log.info { "Review added for show[${reviewInput.showId}]. $review" }
    }

    override suspend fun saveReviews(reviewsInput: List<SubmittedReview>) {
        reviewsInput.forEach { reviewInput ->
            runCatching {
                saveReview(reviewInput)
            }
        }
    }

    override fun getReviewsPerShow(): Publisher<Review> {
        return reviewPublisher
    }

    override suspend fun listReviews(dateRange: DateRange): List<Review> {
        val startTime = Instant.ofEpochSecond(dateRange.start.toEpochSecond(LocalTime.NOON, ZoneOffset.UTC))
        val endTime = Instant.ofEpochSecond(dateRange.end.toEpochSecond(LocalTime.NOON, ZoneOffset.UTC))

        return showIdReviews
            .flatMap { it.value }
            .filter {
                (it.submittedDate?.isAfter(startTime) ?: false) &&
                (it.submittedDate?.isBefore(endTime) ?: false)
            }
            .toFastList()
            .apply {
                log.info { "List reviews. dateRange: $dateRange, size: ${this.size}" }
            }
    }
}
