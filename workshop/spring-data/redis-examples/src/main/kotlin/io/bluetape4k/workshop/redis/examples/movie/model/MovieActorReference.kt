package io.bluetape4k.workshop.redis.examples.movie.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Reference
import org.springframework.data.redis.core.RedisHash
import java.io.Serializable

/**
 * NOTE: 검색조건에 @Reference 이 적용된 속성은 지원하지 않습니다. Redis hashId로 검색해야 합니다.
 *
 * NOTE: [MovieActorReference] 는 지원여부를 검증하기 위해서 만든 것이고, 실제로는 [MovieActor] 처럼 hash id 값으로 연관관계를 맺어야 합니다.
 *
 * 참고: [Persisting References](https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#redis.repositories.references)
 *
 * @property movie [Movie] instance
 * @property actor [Actor] instance
 */
@RedisHash("movie_actor_references")
data class MovieActorReference(
    @Reference val movie: Movie? = null,
    @Reference val actor: Actor? = null,
): Serializable {

    @get:Id
    var hashId: String? = null
}
