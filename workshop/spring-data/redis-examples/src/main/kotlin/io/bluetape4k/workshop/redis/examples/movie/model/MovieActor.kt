package io.bluetape4k.workshop.redis.examples.movie.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import java.io.Serializable

/**
 * [Movie], [Actor]의 many-to-many 연관 관계 정보를 가지는 엔티티입니다. (매핑 테이블 역할)
 *
 * 참고: [Persisting References](https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#redis.repositories.references)
 *
 * @property movieHashId [Movie] hash id
 * @property actorHashId [Actor] hash id
 */
@RedisHash("movie_actors")
data class MovieActor(
    @Indexed val movieHashId: String,
    @Indexed val actorHashId: String,
): Serializable {

    @get:Id
    var hashId: String? = null
}
