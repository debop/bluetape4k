package io.bluetape4k.workshop.graphql.dgs.datafetchers

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import io.bluetape4k.workshop.graphql.dgs.generated.types.Show
import io.bluetape4k.workshop.graphql.dgs.services.ShowService
import kotlinx.coroutines.coroutineScope

@DgsComponent
class ShowDataFetcher(private val showService: ShowService) {

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
    suspend fun shows(@InputArgument titleFilter: String?): List<Show> = coroutineScope {
        titleFilter
            ?.let {
                showService.shows().filter { it.title.contains(titleFilter) }
            }
            ?: showService.shows()
    }
}
