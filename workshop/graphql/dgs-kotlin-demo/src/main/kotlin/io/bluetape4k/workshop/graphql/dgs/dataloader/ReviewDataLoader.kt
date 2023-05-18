package io.bluetape4k.workshop.graphql.dgs.dataloader

import com.netflix.graphql.dgs.DgsDataLoader
import io.bluetape4k.concurrent.futureOf
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.graphql.dgs.generated.types.Review
import io.bluetape4k.workshop.graphql.dgs.services.ReviewService
import org.dataloader.MappedBatchLoader
import java.util.concurrent.CompletionStage

@DgsDataLoader(name = "review")
class ReviewDataLoader(val reviewService: ReviewService): MappedBatchLoader<Int, List<Review>> {

    companion object: KLogging()

    /**
     * This method will be called once, even if multiple datafetchers use the load() method on the DataLoader.
     * This way reviews can be loaded for all the Shows in a single call instead of per individual Show.
     *
     * @param keys  The keys are the showIds.
     */
    override fun load(keys: Set<Int>): CompletionStage<Map<Int, List<Review>>> {
        // key 별로 review 를 가져오는 비동기 작업을 수행한다.
        log.debug { "Async load reviews by show ids: ${keys.joinToString(",")}" }

        return futureOf {
            reviewService.reviewsForShows(keys.toSet())
        }
    }
}
