package io.bluetape4k.workshop.graphql.dgs.dataloaders

import com.netflix.graphql.dgs.DgsDataLoader
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.graphql.dgs.generated.types.Review
import io.bluetape4k.workshop.graphql.dgs.service.ReviewService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.future
import kotlinx.coroutines.newFixedThreadPoolContext
import org.dataloader.BatchLoaderEnvironment
import org.dataloader.MappedBatchLoaderWithContext
import java.util.concurrent.CompletionStage

@DgsDataLoader(name = "reviewWithContext")
class ReviewDataLoaderWithContext(
    private val reviewService: ReviewService,
): MappedBatchLoaderWithContext<Int, List<Review>> {

    companion object: KLogging() {
        private val dispatcher = newFixedThreadPoolContext(16, "review-data-loader")
    }

    /**
     * Show 마다 reviews 를 얻기 위해 SELECT N+1 처럼 동작하지 않고, 한꺼번에 모든 reviews 를 얻도록 한번만 호출됩니다.
     *
     * @param keys 한번에 조회할 show id 들
     * @param env
     * @return show id 별로 reviews 를 담은 map
     */
    override fun load(
        keys: Set<Int>,
        env: BatchLoaderEnvironment,
    ): CompletionStage<Map<Int, List<Review>>> {
        // key 별로 review 를 가져오는 비동기 작업을 수행한다.
        log.debug { "Async load reviews by show ids: ${keys.joinToString(",")}" }

        // Dispatchers.IO 를 써도 되지만, 이렇게 명시적으로 독립된 ThreadPool을 사용하면, Data Loader 별로 격리를 할 수 있다
        return CoroutineScope(dispatcher).future {
            reviewService.reviewsForShows(keys)
        }
    }
}
