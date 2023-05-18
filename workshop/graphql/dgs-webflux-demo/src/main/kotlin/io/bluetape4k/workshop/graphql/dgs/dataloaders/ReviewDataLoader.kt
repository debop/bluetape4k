package io.bluetape4k.workshop.graphql.dgs.dataloaders

import com.netflix.graphql.dgs.DgsDataLoader
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.graphql.dgs.generated.types.Review
import io.bluetape4k.workshop.graphql.dgs.service.ReviewService
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.future.future
import org.dataloader.MappedBatchLoader
import java.util.concurrent.CompletionStage

@DgsDataLoader(name = "review")
class ReviewDataLoader(val reviewService: ReviewService): MappedBatchLoader<Int, List<Review>> {

    companion object: KLogging()

    // Data Loader 별로 독립된 CoroutineScope 를 사용한다.
    private val scope = CoroutineScope(
        Dispatchers.IO + CoroutineName("review-data-loader") + SupervisorJob()
    )

    /**
     * Show 마다 reviews 를 얻기 위해 SELECT N+1 처럼 동작하지 않고, 한꺼번에 모든 reviews 를 얻도록 한번만 호출됩니다.
     *
     * @param keys 한번에 조회할 show id 들
     * @return show id 별로 reviews 를 담은 map
     */
    override fun load(keys: Set<Int>): CompletionStage<Map<Int, List<Review>>> {
        // key 별로 review 를 가져오는 비동기 작업을 수행한다.
        log.debug { "Async load reviews by show ids: ${keys.joinToString(",")}" }

        return scope.future {
            reviewService.reviewsForShows(keys.toSet())
        }
    }
}
