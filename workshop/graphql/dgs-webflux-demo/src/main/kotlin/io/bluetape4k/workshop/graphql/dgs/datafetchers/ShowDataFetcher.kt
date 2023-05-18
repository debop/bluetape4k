package io.bluetape4k.workshop.graphql.dgs.datafetchers

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.graphql.dgs.generated.types.Show
import io.bluetape4k.workshop.graphql.dgs.service.ShowService
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

@DgsComponent
class ShowDataFetcher(private val showService: ShowService) {

    companion object: KLogging()

    /**
     * [Show]의 `title` 속성에 `titleFilter`가 포함되어 있는 모든 [Show]을 반환합니다.
     * [titleFilter] 가 `null` 이면 모든 [Show]를 반환합니다.
     *
     * NOTE: Coroutine을 사용하여 비동기 처리를 수행합니다.
     *
     * @param titleFilter [Show.title]로 검색할 필터
     * @return 검색한 [Show] 목록
     */
    @DgsQuery
    suspend fun shows(
        @InputArgument("titleFilter") titleFilter: String?,
    ): List<Show> = coroutineScope {
        delay(10L)
        log.debug { "Get shows by titleFilter: $titleFilter" }

        when {
            titleFilter.isNullOrEmpty() -> showService.shows()
            else                        -> showService.shows(titleFilter)
        }
    }
}
