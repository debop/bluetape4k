package io.bluetape4k.workshop.graphql.dgs.services

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.info
import io.bluetape4k.workshop.graphql.dgs.generated.types.Review
import io.bluetape4k.workshop.graphql.dgs.generated.types.SubmittedReview
import kotlinx.coroutines.runBlocking
import net.datafaker.Faker
import org.reactivestreams.Publisher
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

interface ReviewService {
    fun reviewsForShow(showId: Int): List<Review>
    fun reviewsForShows(showIds: Collection<Int>): Map<Int, List<Review>>

    fun saveReview(reviewInput: SubmittedReview)
    fun getReviewsPublisher(): Publisher<Review>
}

/**
 * This service emulates a data store.
 * For convenience in the demo we just generate Reviews in memory, but imagine this would be backed by for example a database.
 * If this was indeed backed by a database, it would be very important to avoid the N+1 problem,
 * which means we need to use a DataLoader to call this class.
 */
@Service
class DefaultReviewService(
    @Autowired private val showService: ShowService,
): ReviewService, InitializingBean {

    companion object: KLogging() {
        val faker = Faker()
    }

    private val reviews: MutableMap<Int, MutableList<Review>> = mutableMapOf()
    private lateinit var reviewStream: FluxSink<Review>
    private lateinit var reviewPublisher: Publisher<Review>

    /**
     * Hopefully nobody calls this for multiple shows within a single query, that would indicate the N+1 problem!
     */
    override fun reviewsForShow(showId: Int): List<Review> {
        log.debug { "Reviews for show [$showId]" }
        return reviews.computeIfAbsent(showId) { mutableListOf() }
    }

    /**
     * This is the method we want to call when loading reviews for multiple shows.
     * If this code was backed by a relational database, it would select reviews for all requested shows in a single SQL query.
     */
    override fun reviewsForShows(showIds: Collection<Int>): Map<Int, List<Review>> {
        log.info { "Load reviews for shows ${showIds.joinToString(",")}" }
        return reviews.filter { showIds.contains(it.key) }
    }

    override fun saveReview(reviewInput: SubmittedReview) {
        log.debug { "Save review request. submitted review=$reviewInput" }

        val reviewsForMoview = reviews.computeIfAbsent(reviewInput.showId) { mutableListOf() }
        val review = Review(
            username = reviewInput.username,
            starScore = reviewInput.starScore,
            submittedDate = OffsetDateTime.now()
        )

        reviewsForMoview.add(review)
        reviewStream.next(review)

        log.info { "Review added. showId=${reviewInput.showId}, review=$review" }
    }

    override fun getReviewsPublisher(): Publisher<Review> {
        return reviewPublisher
    }

    override fun afterPropertiesSet() {
        runBlocking {
            // 모든 Show에 대해 랜덤한 수의 Review를 생성해둔다
            showService.shows().forEach { show ->
                val range = 0..faker.number().numberBetween(1, 20)
                val generatedReviews = {
                    range.map {
                        val date = faker.date().past(300, TimeUnit.DAYS).toLocalDateTime()
                        Review(
                            username = faker.internet().username(),
                            starScore = faker.number().numberBetween(0, 6),
                            submittedDate = OffsetDateTime.of(date, ZoneOffset.UTC)
                        )
                    }.toMutableList()
                }

                reviews.computeIfAbsent(show.id) { generatedReviews() }
            }

            // reviewStream 에 Review를 추가하면, reviewPublisher에서 해당 Review를 받을 수 있다.
            val publisher = Flux.create { emitter ->
                reviewStream = emitter
            }
            reviewPublisher = publisher.publish().autoConnect()
        }
    }
}
